package io.sweers.barber;

import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;

import static io.sweers.barber.Kind.FRACTION;
import static io.sweers.barber.Kind.STANDARD;

/**
 * The Barbershop class is what ultimately creates the **$$Barbershop classes called at runtime to
 * style views.
 */
class Barbershop {

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final Map<Integer, FieldBinding> fieldBindings;
    private String parentBarbershop;

    Barbershop(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.fieldBindings = new HashMap<>();
    }

    void setParentBarbershop(String parentBarbershop) {
        this.parentBarbershop = parentBarbershop;
    }

    /**
     * Generates the class code and writes to a new source file.
     *
     * @param filer Annotation filer instance provided by {@link BarberProcessor}
     * @throws IOException
     */
    public void writeToFiler(Filer filer) throws IOException {
        ClassName targetClassName = ClassName.get(classPackage, targetClass);
        TypeSpec.Builder barberShop = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", targetClassName))
                .addMethod(generateStyleMethod())
                .addMethod(generateCheckParentMethod());

        if (parentBarbershop == null) {
            barberShop.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Barber.IBarbershop.class), TypeVariableName.get("T")));
            barberShop.addField(FieldSpec.builder(WeakHashSet.class, "lastStyledTargets", Modifier.PROTECTED).initializer("new $T()", WeakHashSet.class).build());
        } else {
            barberShop.superclass(ParameterizedTypeName.get(ClassName.bestGuess(parentBarbershop), TypeVariableName.get("T")));
        }

        JavaFile javaFile = JavaFile.builder(classPackage, barberShop.build()).build();
        javaFile.writeTo(filer);
    }

    /**
     * This generates the actual style() method implementation for the $$Barbershop class
     * @return A complete MethodSpec implementation for the class's style() method.
     */
    private MethodSpec generateStyleMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("style")
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .returns(void.class)
                .addParameter(TypeVariableName.get("T"), "target", Modifier.FINAL)
                .addParameter(AttributeSet.class, "set", Modifier.FINAL)
                .addParameter(int[].class, "attrs", Modifier.FINAL)
                .addParameter(int.class, "defStyleAttr", Modifier.FINAL)
                .addParameter(int.class, "defStyleRes", Modifier.FINAL);

        if (parentBarbershop != null) {
            builder.beginControlFlow("if (!super.hasStyled(target))")
                    .addStatement("super.style(target, set, attrs, defStyleAttr, defStyleRes)")
                    .addStatement("return")
                    .endControlFlow();
        }

        // Update our latest target
        builder.addStatement("this.lastStyledTargets.add(target)");

        // Don't do anything if there's no AttributeSet instance
        builder.beginControlFlow("if (set == null)")
                .addStatement("return")
                .endControlFlow()

                // Proceed with obtaining the TypedArray if we got here
                .addStatement("$T a = target.getContext().obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)", TypedArray.class);

        for (FieldBinding binding : fieldBindings.values()) {
            // Wrap the styling with if-statement to check if there's a value first, this way we can
            // keep existing default values if there isn't one and don't overwrite them.
            // TODO Remove this if possible to let user specify default values, but I haven't found a way yet.
            builder.beginControlFlow("if (a.hasValue($L))", binding.id);
            if (binding.isMethod) {
                // Call the method directly
                builder.addStatement("target.$L(a.$L)", binding.name, getFormattedStatementForBinding(binding));
            } else {
                builder.addStatement("target.$L = a.$L", binding.name, getFormattedStatementForBinding(binding));
            }
            if (binding.isRequired) {
                builder.nextControlFlow("else")
                        .addStatement("throw new $T(\"Missing required attribute \'$L\' while styling \'$L\'\")", IllegalStateException.class, binding.name, targetClass);
            }
            builder.endControlFlow();
        }

        builder.addStatement("a.recycle()");

        return builder.build();
    }

    private MethodSpec generateCheckParentMethod() {
        MethodSpec.Builder builder = MethodSpec.methodBuilder("hasStyled")
                .returns(boolean.class)
                .addModifiers(Modifier.PROTECTED)
                .addParameter(TypeVariableName.get("T"), "target", Modifier.FINAL)
                .addStatement("return this.lastStyledTargets.contains(target)");

        if (parentBarbershop != null) {
            builder.addAnnotation(Override.class);
        }

        return builder.build();
    }

    private String getFormattedStatementForBinding(FieldBinding binding) {
        String statement;
        if (binding.kind == STANDARD) {
            switch (binding.type) {
                case "java.lang.Integer":
                case "int":
                    statement = "getInt(%d, -1)";
                    break;
                case "boolean":
                    statement = "getBoolean(%d, false)";
                    break;
                case "float":
                    statement = "getFloat(%d, -1f)";
                    break;
                case "java.lang.String":
                    statement = "getString(%d)";
                    break;
                case "java.lang.CharSequence":
                    statement = "getText(%d)";
                    break;
                case "android.graphics.drawable.Drawable":
                    statement = "getDrawable(%d)";
                    break;
                case "java.lang.CharSequence[]":
                    statement = "getTextArray(%d)";
                    break;
                case "android.content.res.ColorStateList":
                    statement = "getColorStateList(%d)";
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid type %s for id %d", binding.type, binding.id));
            }
        } else {
            switch (binding.kind) {
                case COLOR:
                    statement = "getColor(%d, -1)";
                    break;
                case FRACTION:
                    statement = "getFraction(%d, " + binding.fractBase + ", " + binding.fractPBase + ", -1f)";
                    break;
                case INTEGER:
                    statement = "getInteger(%d, -1)";
                    break;
                case DIMEN:
                    statement = "getDimension(%d, -1f)";
                    break;
                case DIMEN_PIXEL_SIZE:
                    statement = "getDimensionPixelSize(%d, -1)";
                    break;
                case DIMEN_PIXEL_OFFSET:
                    statement = "getDimensionPixelOffset(%d, -1)";
                    break;
                case RES_ID:
                    statement = "getResourceId(%d, -1)";
                    break;
                case NON_RES_STRING:
                    statement = "getNonResourceString(%d)";
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid attrType %s for id %d", binding.kind.name(), binding.id));
            }
        }


        return String.format(statement, binding.id);
    }

    public void createAndAddBinding(Element element) {
        int id = element.getAnnotation(StyledAttr.class).value();
        String name;
        String type;
        if (element.getKind() == ElementKind.FIELD) {
            name = element.getSimpleName().toString();
            type = element.asType().toString();
        } else {
            ExecutableElement executableElement = (ExecutableElement) element;
            name = executableElement.getSimpleName().toString();
            type = executableElement.getParameters().get(0).asType().toString();
        }
        if (fieldBindings.containsKey(id)) {
            throw new IllegalStateException(String.format("Duplicate ID assigned for field %s and %s", name, fieldBindings.get(id).name));
        }

        StyledAttr instance = element.getAnnotation(StyledAttr.class);
        FieldBinding binding = new FieldBinding(name, type, id, instance.kind());
        binding.isRequired = element.getAnnotation(Required.class) != null;
        if (element.getKind() == ElementKind.METHOD) {
            binding.isMethod = true;
        }
        if (binding.kind == FRACTION) {
            binding.fractBase = instance.base();
            binding.fractPBase = instance.pbase();
        }

        fieldBindings.put(id, binding);
    }

    public static class FieldBinding {

        private final String name;
        private final String type;
        private int id;
        private Kind kind;
        private boolean isMethod;
        private boolean isRequired;

        // Fractions
        private int fractBase;
        private int fractPBase;

        public FieldBinding(String name, String type, int id, Kind kind) {
            this.name = name;
            this.type = type;
            this.id = id;
            this.kind = kind;
        }
    }
}
