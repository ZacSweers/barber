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
    private final Map<Integer, StyleableBinding> styleableBindings;
    private final Map<String, AndroidAttrBinding> androidAttrBindings;
    private String parentBarbershop;

    Barbershop(String classPackage, String className, String targetClass) {
        this.classPackage = classPackage;
        this.className = className;
        this.targetClass = targetClass;
        this.styleableBindings = new HashMap<>();
        this.androidAttrBindings = new HashMap<>();
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
                .endControlFlow();

        builder.beginControlFlow("if (attrs != null)");
        // Proceed with obtaining the TypedArray if we got here
        builder.addStatement("$T a = target.getContext().obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)", TypedArray.class);


        for (StyleableBinding binding : styleableBindings.values()) {
            // Wrap the styling with if-statement to check if there's a value first, this way we can
            // keep existing default values if there isn't one and don't overwrite them.
            // TODO Remove this if possible to let user specify default values, but I haven't found a way yet.
            builder.beginControlFlow("if (a.hasValue($L))", binding.id);
            if (binding.isMethod) {
                // Call the method directly
                builder.addStatement("target.$L(a.$L)", binding.name, binding.getFormattedStatement());
            } else {
                builder.addStatement("target.$L = a.$L", binding.name, binding.getFormattedStatement());
            }
            if (binding.isRequired) {
                builder.nextControlFlow("else")
                        .addStatement("throw new $T(\"Missing required attribute \'$L\' while styling \'$L\'\")", IllegalStateException.class, binding.name, targetClass);
            }
            builder.endControlFlow();
        }

        builder.addStatement("a.recycle()");
        builder.endControlFlow();

        for (AndroidAttrBinding binding : androidAttrBindings.values()) {
            if (binding.isMethod) {
                // Call the method directly
                builder.addStatement("target.$L(set.$L)", binding.name, binding.getFormattedStatement());
            } else {
                builder.addStatement("target.$L = set.$L", binding.name, binding.getFormattedStatement());
            }
        }

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

    public void createAndAddStyleableBinding(Element element) {
        StyledAttr instance = element.getAnnotation(StyledAttr.class);
        int id = instance.value();
        StyleableBinding styleableBinding = new StyleableBinding(element, id, instance.kind());
        if (styleableBindings.containsKey(id)) {
            throw new IllegalStateException(String.format("Duplicate ID assigned for field %s and %s", styleableBinding.name, styleableBindings.get(id).name));
        }

        if (styleableBinding.kind == FRACTION) {
            styleableBinding.fractBase = instance.base();
            styleableBinding.fractPBase = instance.pbase();
        }

        styleableBindings.put(id, styleableBinding);
    }

    public void createAndAddAndroidAttrBinding(Element element) {
        AndroidAttr instance = element.getAnnotation(AndroidAttr.class);
        String attr = instance.value();
        String namespace = instance.namespace();
        AttrSetKind kind = instance.kind();
        AndroidAttrBinding androidAttrBinding = new AndroidAttrBinding(element, attr, namespace, kind);
        if (androidAttrBindings.containsKey(attr)) {
            throw new IllegalStateException(String.format("Duplicate attr assigned for field %s and %s", androidAttrBinding.name, androidAttrBindings.get(attr).name));
        }

        androidAttrBindings.put(attr, androidAttrBinding);
    }

    private abstract static class Binding {
        final String name;
        final String type;
        final boolean isMethod;
        final boolean isRequired;

        public Binding(Element element) {
            if (element.getKind() == ElementKind.FIELD) {
                name = element.getSimpleName().toString();
                type = element.asType().toString();
            } else {
                ExecutableElement executableElement = (ExecutableElement) element;
                name = executableElement.getSimpleName().toString();
                type = executableElement.getParameters().get(0).asType().toString();
            }

            isRequired = element.getAnnotation(Required.class) != null;
            isMethod = element.getKind() == ElementKind.METHOD;
        }

        public abstract String getFormattedStatement();
    }

    private static class StyleableBinding extends Binding {

        final int id;
        final Kind kind;

        // Fractions
        int fractBase;
        int fractPBase;

        StyleableBinding(Element element, int id, Kind kind) {
            super(element);
            this.id = id;
            this.kind = kind;
        }

        @Override
        public String getFormattedStatement() {
            String statement;
            if (kind == STANDARD) {
                switch (type) {
                    case "java.lang.Integer":
                    case "int":
                        statement = "getInt(%d, -1)";
                        break;
                    case "java.lang.Boolean":
                    case "boolean":
                        statement = "getBoolean(%d, false)";
                        break;
                    case "java.lang.Float":
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
                        throw new IllegalArgumentException(String.format("Invalid type %s for id %d", type, id));
                }
            } else {
                switch (kind) {
                    case COLOR:
                        statement = "getColor(%d, -1)";
                        break;
                    case FRACTION:
                        statement = "getFraction(%d, " + fractBase + ", " + fractPBase + ", -1f)";
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
                        throw new IllegalArgumentException(String.format("Invalid attrType %s for id %d", kind.name(), id));
                }
            }

            return String.format(statement, id);
        }
    }

    private static class AndroidAttrBinding extends Binding {
        final String attrName;
        final String namespace;
        private AttrSetKind kind;

        AndroidAttrBinding(Element element, String attrName, String namespace, AttrSetKind kind) {
            super(element);
            this.attrName = attrName;
            this.namespace = namespace;
            this.kind = kind;
        }

        @Override
        public String getFormattedStatement() {
            String statement;
            if (kind == AttrSetKind.STANDARD) {
                switch (type) {
                    case "java.lang.Integer":
                    case "int":
                        statement = "getAttributeIntValue(\"%s\", \"%s\", -1)";
                        break;
                    case "java.lang.Boolean":
                    case "boolean":
                        statement = "getAttributeBooleanValue(\"%s\", \"%s\", false)";
                        break;
                    case "java.lang.Float":
                    case "float":
                        statement = "getAttributeFloatValue(\"%s\", \"%s\", -1f)";
                        break;
                    case "java.lang.CharSequence":
                    case "java.lang.String":
                        statement = "getAttributeValue(\"%s\", \"%s\")";
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Invalid type \"%s\" for id \"%s\"", type, attrName));
                }
            } else {
                switch (kind) {
                    case U_INT:
                        statement = "getAttributeUnsignedIntValue(\"%s\", \"%s\", -1)";
                        break;
                    case RESOURCE:
                        statement = "getAttributeResourceValue(\"%s\", \"%s\", -1)";
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Invalid type \"%s\" for id \"%s\"", kind.name(), attrName));
                }
            }

            return String.format(statement, namespace, attrName);
        }
    }
}
