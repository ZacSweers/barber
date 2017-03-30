package io.sweers.barber;

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

import static io.sweers.barber.Kind.DIMEN;
import static io.sweers.barber.Kind.FRACTION;
import static io.sweers.barber.Kind.RES_ID;
import static io.sweers.barber.Kind.STANDARD;

/**
 * The Barbershop class is what ultimately creates the **$$Barbershop classes called at runtime to
 * style views.
 */
class Barbershop {

    private final static ClassName RESOURCES_CLASS_NAME = ClassName.get("android.content.res", "Resources");
    private final static ClassName TYPED_ARRAY_CLASS_NAME = ClassName.get("android.content.res", "TypedArray");
    private final static ClassName ATTRIBUTE_SET_CLASS_NAME = ClassName.get("android.util", "AttributeSet");
    private final static ClassName I_BARBER_SHOP_CLASS_NAME = ClassName.get("io.sweers.barber", "Barber", "IBarbershop");

    private final String classPackage;
    private final String className;
    private final String targetClass;
    private final Map<Integer, StyleableBinding> styleableBindings;
    private final Map<String, AndroidAttrBinding> androidAttrBindings;
    private String parentBarbershop;
    private boolean hasDefaults = false;

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
    void writeToFiler(Filer filer) throws IOException {
        ClassName targetClassName = ClassName.get(classPackage, targetClass);
        TypeSpec.Builder barberShop = TypeSpec.classBuilder(className)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(TypeVariableName.get("T", targetClassName))
                .addMethod(generateStyleMethod())
                .addMethod(generateCheckParentMethod());

        if (parentBarbershop == null) {
            barberShop.addSuperinterface(ParameterizedTypeName.get(I_BARBER_SHOP_CLASS_NAME, TypeVariableName.get("T")));
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
                .addParameter(ATTRIBUTE_SET_CLASS_NAME, "set", Modifier.FINAL)
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

        if (!styleableBindings.isEmpty()) {
            if (hasDefaults) {
                builder.addStatement("$T res = target.getContext().getResources()", RESOURCES_CLASS_NAME);
            }

            // Proceed with obtaining the TypedArray if we got here
            builder.addStatement("$T a = target.getContext().obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes)", TYPED_ARRAY_CLASS_NAME);


            builder.addCode("// Retrieve custom attributes\n");
            for (StyleableBinding binding : styleableBindings.values()) {

                if (binding.kind == RES_ID && binding.hasDefaultValue() && !binding.isRequired) {
                    // No overhead in resource retrieval for these, so no need to wrap in a hasValue check
                    builder.addStatement(generateSetterStatement(binding), binding.name, binding.getFormattedStatement("a."));
                    continue;
                }

                // Wrap the styling with if-statement to check if there's a value first, this way we can
                // keep existing default values if there isn't one and don't overwrite them.
                builder.beginControlFlow("if (a.hasValue($L))", binding.id);
                builder.addStatement(generateSetterStatement(binding), binding.name, binding.getFormattedStatement("a."));

                if (binding.isRequired) {
                    builder.nextControlFlow("else");
                    builder.addStatement("throw new $T(\"Missing required attribute \'$L\' while styling \'$L\'\")", IllegalStateException.class, binding.name, targetClass);
                } else if (binding.hasDefaultValue()) {
                    builder.nextControlFlow("else");
                    if (binding.kind != FRACTION && binding.kind != DIMEN && ("float".equals(binding.type) || "java.lang.Float".equals(binding.type))) {
                        // Getting a float from resources is nasty
                        builder.addStatement(generateSetterStatement(binding), binding.name, "Barber.resolveFloatResource(res, " + binding.defaultValue + ")");
                    } else if ("android.graphics.drawable.Drawable".equals(binding.type)) {
                        // Compatibility using ResourcesCompat.getDrawable(...)
                        builder.addStatement(generateSetterStatement(binding), binding.name,
                                "android.support.v4.content.res.ResourcesCompat.getDrawable(res, "
                                        + binding.defaultValue
                                        + ", target.getContext().getTheme())");
                    } else {
                        builder.addStatement(generateSetterStatement(binding), binding.name, generateResourceStatement(binding, "res.", true));
                    }
                }
                builder.endControlFlow();
            }

            builder.addStatement("a.recycle()");
        }

        if (!androidAttrBindings.isEmpty()) {
            builder.addCode("// Retrieve android attr values\n");
            for (AndroidAttrBinding binding : androidAttrBindings.values()) {
                builder.addStatement(generateSetterStatement(binding), binding.name, binding.getFormattedStatement("set."));
            }
        }

        return builder.build();
    }

    /**
     * Convenience generator for method/field setting
     */
    private static String generateSetterStatement(Binding binding) {
        // Call the method directly
        if (binding.isMethod) {
            return "target.$L($L)";
        } else {
            return "target.$L = $L";
        }
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

    void createAndAddStyleableBinding(Element element) {
        StyleableBinding binding = new StyleableBinding(element);
        if (styleableBindings.containsKey(binding.id)) {
            throw new IllegalStateException(String.format("Duplicate ID assigned for field %s and %s", binding.name, styleableBindings.get(binding.id).name));
        } else {
            styleableBindings.put(binding.id, binding);
        }

        if (!hasDefaults && binding.hasDefaultValue()) {
            hasDefaults = true;
        }
    }

    void createAndAddAndroidAttrBinding(Element element) {
        AndroidAttrBinding binding = new AndroidAttrBinding(element);
        if (androidAttrBindings.containsKey(binding.attrName)) {
            throw new IllegalStateException(String.format("Duplicate attr assigned for field %s and %s", binding.name, androidAttrBindings.get(binding.attrName).name));
        } else {
            androidAttrBindings.put(binding.attrName, binding);
        }

        if (!hasDefaults && binding.hasDefaultValue()) {
            hasDefaults = true;
        }
    }

    private abstract static class Binding {
        final String name;
        final String type;
        final boolean isMethod;
        final boolean isRequired;
        final int defaultValue;
        final boolean hasDefaultValue;

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
            defaultValue = resolveDefault(element);
            hasDefaultValue = defaultValue != -1;
        }

        private int resolveDefault(Element element) {
            StyledAttr styledAttr = element.getAnnotation(StyledAttr.class);
            if (styledAttr != null) {
                return styledAttr.defaultValue();
            }

            return -1;
        }

        public boolean hasDefaultValue() {
            return hasDefaultValue;
        }

        @SuppressWarnings("unused")
        public abstract String getFormattedStatement(String prefix);
    }

    private static class StyleableBinding extends Binding {

        final int id;
        final Kind kind;

        // Fractions
        int fractBase;
        int fractPBase;

        StyleableBinding(Element element) {
            super(element);
            StyledAttr instance = element.getAnnotation(StyledAttr.class);
            this.id = instance.value();
            this.kind = instance.kind();
            if (kind == FRACTION) {
                fractBase = instance.base();
                fractPBase = instance.pbase();
            }
        }

        @Override
        public String getFormattedStatement(String prefix) {
            return generateResourceStatement(this, prefix, false);
        }
    }

    private static String generateResourceStatement(StyleableBinding binding, String prefix, boolean forRes) {
        String statement;
        Kind kindToSwitchOn = binding.kind;

        if (forRes) {
            if (kindToSwitchOn == Kind.STANDARD && ("int".equals(binding.type) || "java.lang.Integer".equals(binding.type))) {
                // These need to call Resources#getInteger()
                kindToSwitchOn = Kind.INTEGER;
            } else if (kindToSwitchOn == Kind.NON_RES_STRING) {
                kindToSwitchOn = Kind.STANDARD;
            }
        }

        if (kindToSwitchOn == STANDARD) {
            switch (binding.type) {
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
                    throw new IllegalArgumentException(String.format("Invalid type %s for id %d", binding.type, binding.id));
            }
        } else {
            switch (kindToSwitchOn) {
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
                    statement = "getResourceId(%d, " + (binding.hasDefaultValue() ? binding.defaultValue : "-1") + ")";
                    break;
                case NON_RES_STRING:
                    statement = "getNonResourceString(%d)";
                    break;
                default:
                    throw new IllegalArgumentException(String.format("Invalid type %s for id %d", binding.type, binding.id));
            }
        }

        return prefix + String.format(
                forRes ? chompLastParam(statement) : statement,
                forRes ? binding.defaultValue : binding.id
        );
    }

    /**
     * Remove the last param if there are multiple, used for when we want to adapt a resource getter
     * to one used with {@link android.content.res.Resources}
     */
    private static String chompLastParam(String input) {
        int lastCommaIndex = input.lastIndexOf(',');
        if (lastCommaIndex == -1) {
            return input;
        } else {
            return input.substring(0, lastCommaIndex) + ")";
        }
    }

    private static class AndroidAttrBinding extends Binding {
        final String attrName;
        final AttrSetKind kind;

        AndroidAttrBinding(Element element) {
            super(element);

            if (isRequired) {
                throw new IllegalStateException("Cannot use @Required annotations with @AndroidAttr!");
            }

            AndroidAttr instance = element.getAnnotation(AndroidAttr.class);
            this.attrName = instance.value();
            this.kind = instance.kind();
        }

        @Override
        public String getFormattedStatement(String prefix) {
            String statement;
            if (kind == AttrSetKind.STANDARD) {
                switch (type) {
                    case "java.lang.Integer":
                    case "int":
                        statement = "getAttributeIntValue(%s, %s, -1)";
                        break;
                    case "java.lang.Boolean":
                    case "boolean":
                        statement = "getAttributeBooleanValue(%s, %s, false)";
                        break;
                    case "java.lang.Float":
                    case "float":
                        statement = "getAttributeFloatValue(%s, %s, -1f)";
                        break;
                    case "java.lang.CharSequence":
                    case "java.lang.String":
                        statement = "getAttributeValue(%s, %s)";
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Invalid type \"%s\" for id \"%s\"", type, attrName));
                }
            } else {
                switch (kind) {
                    case U_INT:
                        statement = "getAttributeUnsignedIntValue(%s, %s, -1)";
                        break;
                    case RESOURCE:
                        statement = "getAttributeResourceValue(%s, %s, -1)";
                        break;
                    default:
                        throw new IllegalArgumentException(String.format("Invalid type \"%s\" for id \"%s\"", kind.name(), attrName));
                }
            }

            return prefix + String.format(statement, "ANDROID_ATTR_NAMESPACE", "\"" + attrName + "\"");
        }
    }
}
