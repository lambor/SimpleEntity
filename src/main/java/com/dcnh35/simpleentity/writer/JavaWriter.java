package com.dcnh35.simpleentity.writer;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.Filer;
import javax.lang.model.element.Modifier;

import com.squareup.javapoet.*;

import static com.dcnh35.simpleentity.util.StringUtil.*;

public class JavaWriter<T extends JavaWriter.Codable> {

    private ArrayList<T> dataSource;

    // public JavaWriter() {}

    public void setDataSource(List<T> dataSource) {
        if (this.dataSource == null)
            this.dataSource = new ArrayList<>();
        else
            this.dataSource.clear();
        this.dataSource.addAll(dataSource);
    }

    public void generateEntities(Filer filer) {
        for (T data : dataSource) {
            generateEntity(data, filer);
        }
    }

    private void generateInnerClass(TypeSpec.Builder builder, Codable.EntityClass clazz, boolean serializable) {
        if (clazz.innerClasses == null) return;
        for (Codable.EntityClass iclazz : clazz.innerClasses) {
            TypeSpec.Builder iBuilder = TypeSpec.classBuilder(iclazz.className)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC);
            if(serializable) iBuilder.addSuperinterface(Serializable.class);
            for (Codable.EntityField iField : iclazz.fields)
                iBuilder.addField(iField.fieldType, iField.fieldName, Modifier.PUBLIC);
            builder.addType(iBuilder.build());
            generateInnerClass(builder, iclazz,serializable);
        }
    }

    private void generateEntity(T data, Filer filer) {
        Codable.EntityClass clazz = data.getEntityClass();
        boolean serializable = data.isSerializable();

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(clazz.className);
        classBuilder.addModifiers(Modifier.PUBLIC);
        if (serializable) classBuilder.addSuperinterface(Serializable.class);
        ArrayList<Codable.EntityField> privateFields = new ArrayList<>();

        if (clazz.fields == null) return;

        for (Codable.EntityField field : clazz.fields) {
            boolean isPublic = field.isPublic;
            boolean isRenamed = field.isRenamed;

            FieldSpec.Builder fieldBuilder = FieldSpec.builder(field.fieldType, lowerFirstChar(field.fieldName), isPublic ? Modifier.PUBLIC : Modifier.PRIVATE);
            if (isRenamed) {
                fieldBuilder.addAnnotation(ClassName.get("com.google.gson.annotations", "SerializedName"));
                fieldBuilder.addJavadoc("@SerializedName(\"$s\")",field.serizableName);
            }
            classBuilder.addField(fieldBuilder.build());

            if (!isPublic) privateFields.add(field);
        }

        for (Codable.EntityField privateField : privateFields) {
            classBuilder.addMethod(getSetter(privateField));
            classBuilder.addMethod(getGetter(privateField));
        }

        generateInnerClass(classBuilder, clazz,serializable);

        // Write file
        try {
            JavaFile.builder(data.getPackageName(), classBuilder.build()).build().writeTo(filer);
//			JavaFile.builder(data.getPackageName(), classBuilder.build()).build().writeTo(System.out); //test
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private MethodSpec getGetter(Codable.EntityField field) {
        MethodSpec getter = MethodSpec.methodBuilder("get" + upperFirstChar(field.fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addStatement("return this.$N", field.fieldName)
                .returns(field.fieldType)
                .build();
        return getter;
    }

    private MethodSpec getSetter(Codable.EntityField field) {
        MethodSpec setter = MethodSpec.methodBuilder("set" + upperFirstChar(field.fieldName))
                .addModifiers(Modifier.PUBLIC)
                .addParameter(field.fieldType, field.fieldName)
                .addStatement("this.$N = $N", field.fieldName, field.fieldName)
                .build();
        return setter;
    }

    public static interface Codable {

        EntityClass getEntityClass();

        String getPackageName();

        boolean isSerializable();

        public class EntityField {
            public String fieldName;
            public String serizableName;
            public TypeName fieldType;
            public boolean isPublic;
            public boolean isRenamed;

            public EntityField(String fieldName, TypeName fieldType, boolean isPublic) {
                super();
                this.fieldName = fieldName;
                this.fieldType = fieldType;
                this.isPublic = isPublic;
            }

            public void setSerizableName(String oldName, String newName) {
                isRenamed = true;
                fieldName = newName;
                serizableName = oldName;
            }
        }

        public class EntityClass {
            public List<EntityField> fields;
            public List<EntityClass> innerClasses;
            public String className;

            public EntityClass(String className) {
                this(className, new ArrayList<EntityField>(), new ArrayList<EntityClass>());
            }

            public EntityClass(String className, List<EntityField> fields,
                               List<EntityClass> innerClasses) {
                super();
                this.className = className;
                this.fields = fields;
                this.innerClasses = innerClasses;
            }
        }
    }
    // public static class Builder<K>{
    // private static JavaWriter writer;
    //
    // static {
    // if(writer == null) writer = new JavaWriter();
    // }
    //
    // public Builder setDataSource(List<T>)
    // }
}
