package com.dcnh35.simpleentity;

import com.dcnh35.simpleentity.annotations.EntitiesConfig;
import com.dcnh35.simpleentity.annotations.EntityConfig;
import com.dcnh35.simpleentity.exception.IllegalJsonStringException;
import com.dcnh35.simpleentity.parser.JsonWrapper;
import com.dcnh35.simpleentity.writer.JavaWriter;
import com.google.auto.service.AutoService;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by lambor on 16-4-21.
 */
@AutoService(Processor.class)
@SupportedAnnotationTypes({"com.dcnh35.simpleentity.annotations.EntitiesConfig","com.dcnh35.simpleentity.annotations.EntityConfig"})
public class SimpleEntityProcessor extends AbstractProcessor{

    private Types typeUtils;
    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        typeUtils = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<?extends Element> elements = roundEnv.getElementsAnnotatedWith(EntitiesConfig.class);

        JavaWriter<JsonWrapper> writer = new JavaWriter<>();
        List<JsonWrapper> datas = new ArrayList<>();

        for(Element element : elements) {
            EntitiesConfig global = element.getAnnotation(EntitiesConfig.class);
            if(!element.getKind().equals(ElementKind.CLASS)) {
                messager.printMessage(Diagnostic.Kind.ERROR,"EntitiesConfig annotation must annotate on class type");
                return true;
            }
            if(!global.switchGenerate()) return true; //if not turn switch on, then not generate entity code.

            for(Element innerElement : element.getEnclosedElements()) {
                EntityConfig local = innerElement.getAnnotation(EntityConfig.class);
                if(local!=null) {
                    try {
                        JsonWrapper wrapper = new JsonWrapper((VariableElement) innerElement,global);
                        datas.add(wrapper);
                    } catch (IllegalJsonStringException e) {
//                        messager.printMessage(Diagnostic.Kind.ERROR,"[IllegalJsonStringException]" + e.getMessage(),innerElement);
                        e.printStackTrace();
                        return true;
                    } catch (IllegalArgumentException e) {
                        messager.printMessage(Diagnostic.Kind.ERROR,"[IllegalArgumentException]" + e.getMessage(),innerElement);
                        e.printStackTrace();
                        return true;
                    }
                }
            }
        }
        writer.setDataSource(datas);
        writer.generateEntities(filer);

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
