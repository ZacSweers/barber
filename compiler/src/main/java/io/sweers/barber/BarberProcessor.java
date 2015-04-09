package io.sweers.barber;

import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class BarberProcessor extends AbstractProcessor {

    public static Processor instance;
    public Types typeUtils;
    public Elements elementUtils;
    public Filer filer;
    public Messager messager;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<String>() {{
            add(StyledAttr.class.getCanonicalName());
            add(Required.class.getCanonicalName());
            add(AndroidAttr.class.getCanonicalName());
        }};
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        instance = this;
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, Barbershop> targetClassMap = new LinkedHashMap<>();
        Set<String> erasedTargetNames = new LinkedHashSet<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(StyledAttr.class)) {
            try {
                if (element.getKind() != ElementKind.FIELD && element.getKind() != ElementKind.METHOD) {
                    error(element, "StyledAttribute annotations can only be applied to fields or methods!");
                    return false;
                }
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                Barbershop barbershop = getOrCreateBarber(targetClassMap, enclosingElement, erasedTargetNames);
                barbershop.createAndAddStyleableBinding(element);
            } catch (Exception e) {
                error(element, "%s", e.getMessage());
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(AndroidAttr.class)) {
            try {
                if (element.getKind() != ElementKind.FIELD && element.getKind() != ElementKind.METHOD) {
                    error(element, "AndroidAttr annotations can only be applied to fields or methods!");
                    return false;
                }
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                Barbershop barbershop = getOrCreateBarber(targetClassMap, enclosingElement, erasedTargetNames);
                barbershop.createAndAddAndroidAttrBinding(element);
            } catch (Exception e) {
                error(element, "%s", e.getMessage());
            }
        }

        for (Map.Entry<TypeElement, Barbershop> entry : targetClassMap.entrySet()) {
            String parentClassFqcn = findParentFqcn(entry.getKey(), erasedTargetNames);
            if (parentClassFqcn != null) {
                entry.getValue().setParentBarbershop(parentClassFqcn + Barber.SUFFIX);
            }
        }

        for (Barbershop barbershop : targetClassMap.values()) {
            try {
                barbershop.writeToFiler(filer);
            } catch (IOException e) {
                messager.printMessage(Diagnostic.Kind.ERROR, e.getMessage());
            }
        }

        return true;
    }

    private Barbershop getOrCreateBarber(Map<TypeElement, Barbershop> targetClassMap, TypeElement enclosingElement, Set<String> erasedTargetNames) {
        Barbershop barbershop = targetClassMap.get(enclosingElement);
        if (barbershop == null) {
            String targetType = enclosingElement.getQualifiedName().toString();
            String classPackage = getPackageName(enclosingElement);
            String className = getClassName(enclosingElement, classPackage) + Barber.SUFFIX;
            barbershop = new Barbershop(classPackage, className, targetType);
            targetClassMap.put(enclosingElement, barbershop);
            erasedTargetNames.add(enclosingElement.toString());
        }

        return barbershop;
    }

    /**
     * Finds the parent barbershop type in the supplied set, if any.
     */
    private String findParentFqcn(TypeElement typeElement, Set<String> parents) {
        TypeMirror type;
        while (true) {
            type = typeElement.getSuperclass();
            if (type.getKind() == TypeKind.NONE) {
                return null;
            }
            typeElement = (TypeElement) ((DeclaredType) type).asElement();
            if (parents.contains(typeElement.toString())) {
                String packageName = getPackageName(typeElement);
                return packageName + "." + getClassName(typeElement, packageName);
            }
        }
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }

    private static String getClassName(TypeElement type, String packageName) {
        int packageLen = packageName.length() + 1;
        return type.getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private String getPackageName(TypeElement type) {
        return elementUtils.getPackageOf(type).getQualifiedName().toString();
    }
}
