package com.fopost.sdk.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import java.lang.reflect.RecordComponent;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.ArrayList;
import java.util.List;

/** The single configured {@link ObjectMapper} the SDK decodes and encodes with. */
public final class Json {

    private Json() {}

    public static final ObjectMapper MAPPER = create();

    private static ObjectMapper create() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        mapper.setAnnotationIntrospector(new CamelCaseAliases());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // A field the server adds must never break an older client.
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return mapper;
    }

    /**
     * Accepts both wire spellings for every property.
     *
     * <p>The API is not consistent about its casing — posts and labels come back snake_case,
     * accounts, webhooks and automations camelCase, workspaces a mix. Models are declared
     * snake_case and every property additionally answers to its camelCase spelling, so one
     * record covers whichever the endpoint happens to send.
     */
    private static final class CamelCaseAliases extends JacksonAnnotationIntrospector {

        private static final long serialVersionUID = 1L;

        @Override
        public List<PropertyName> findPropertyAliases(Annotated annotated) {
            List<PropertyName> aliases = super.findPropertyAliases(annotated);
            String camel = declaredName(annotated);
            if (camel == null || camel.isEmpty() || camel.indexOf('_') >= 0 || !hasUpperCase(camel)) {
                return aliases;
            }
            List<PropertyName> merged = aliases == null ? new ArrayList<>() : new ArrayList<>(aliases);
            merged.add(PropertyName.construct(camel));
            return merged;
        }

        /** A record's constructor parameters carry no name, so read it off the component instead. */
        private static String declaredName(Annotated annotated) {
            if (annotated instanceof AnnotatedParameter parameter) {
                Class<?> owner = parameter.getDeclaringClass();
                if (owner != null && owner.isRecord()) {
                    RecordComponent[] components = owner.getRecordComponents();
                    int index = parameter.getIndex();
                    return index < components.length ? components[index].getName() : null;
                }
                return null;
            }
            return annotated.getName();
        }

        private static boolean hasUpperCase(String name) {
            for (int i = 0; i < name.length(); i++) {
                if (Character.isUpperCase(name.charAt(i))) {
                    return true;
                }
            }
            return false;
        }
    }
}
