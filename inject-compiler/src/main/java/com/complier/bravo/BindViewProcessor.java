package com.complier.bravo;

import com.anno.bravo.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

/**
 * Created by bravo.lee on 2017/10/24.
 */
@AutoService(Processor.class)
public class BindViewProcessor extends AbstractProcessor {

    private Elements elementUtils;

    private Types typeUtils;

    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        elementUtils = processingEnvironment.getElementUtils();
        typeUtils = processingEnvironment.getTypeUtils();
        filer = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> set = new LinkedHashSet<>();
        set.add(BindView.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Map<TypeElement, List<FieldViewBinding>> targetMap = new HashMap<>();
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        for (Element element : elements) {
            FileUtils.print("elment   " + element.getSimpleName().toString());
            TypeElement enClosingElement = (TypeElement) element.getEnclosingElement();
            List<FieldViewBinding> viewBindingList = targetMap.get(enClosingElement);
            if (viewBindingList == null) {
                viewBindingList = new ArrayList<>();
                targetMap.put(enClosingElement, viewBindingList);
            }


            int id = element.getAnnotation(BindView.class).value();
            String fieldName = element.getSimpleName().toString();
            TypeMirror typeMirror = element.asType();
            FieldViewBinding fieldViewBinding = new FieldViewBinding(fieldName, typeMirror, id);

            viewBindingList.add(fieldViewBinding);
        }
        for (Map.Entry<TypeElement, List<FieldViewBinding>> item : targetMap.entrySet()) {
            List<FieldViewBinding> viewBindingList = item.getValue();
            if (viewBindingList == null || viewBindingList.size() == 0) {
                continue;
            }
            TypeElement enClosingElement = item.getKey();
            String packageName = getPackageName(enClosingElement);
            String complite = getClassName(enClosingElement, packageName);

            ClassName className = ClassName.bestGuess(complite);
            ClassName viewBinder = ClassName.get("com.bravo.inject", "ViewBinder");

            TypeSpec.Builder result = TypeSpec.classBuilder(complite + "$$ViewBinder")
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(TypeVariableName.get("T", className))
                    .addSuperinterface(ParameterizedTypeName.get(viewBinder, className));

            MethodSpec.Builder methodBuider = MethodSpec.methodBuilder("bind")
                    .addModifiers(Modifier.PUBLIC)
                    .returns(TypeName.VOID)
                    .addAnnotation(Override.class)
                    .addParameter(className, "target", Modifier.FINAL);

            for (int i = 0; i < viewBindingList.size(); i++) {
                FieldViewBinding fieldViewBinding = viewBindingList.get(i);
                String packageNameString = fieldViewBinding.getType().toString();
                ClassName viewClass = ClassName.bestGuess(packageNameString);
                //View v = findViewByID();
                methodBuider.addStatement("target.$L=($T)target.findViewById($L)", fieldViewBinding.getName()
                        , viewClass, fieldViewBinding.getResId());
            }
            result.addMethod(methodBuider.build());

            try {
                JavaFile.builder(packageName, result.build())
                        .addFileComment("auto create make")
                        .build().writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private String getClassName(TypeElement enClosingElement, String packageName) {
        int packageLength = packageName.length() + 1;

        return enClosingElement.getQualifiedName().toString().substring(packageLength).replace(".", "$");
    }

    private String getPackageName(TypeElement enClosingElement) {
        return elementUtils.getPackageOf(enClosingElement).getQualifiedName().toString();
    }
}
