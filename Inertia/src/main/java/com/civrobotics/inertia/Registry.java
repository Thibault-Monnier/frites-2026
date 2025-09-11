package com.civrobotics.inertia;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registry {

    private static final JSONUtility jsonUtil = new JSONUtility();
    private final String destinationFile;
    private final Class<?> currentClass;
    private final Object current;
    private Map<String, Object> registry;


    /**
     * Create a new Registry instance, current should be an instance of the class where are @Savable
     *
     * @param destinationFile The name and extension of the saved file
     * @param current         The instance of the class where are @Savable
     */
    public Registry(String destinationFile, Object current) {
        this.destinationFile = destinationFile;
        this.currentClass = current.getClass();
        this.current = current;

        try {
            this.registry = jsonUtil.getAllVariablesFromJSON(this.destinationFile);
        } catch (IOException e) {
            this.registry = new HashMap<>();
        }

        loadSavables();

    }

    /**
     * Save all variables annotated with @Savable in the file
     */
    public void saveSavables() {
        for (Field field : currentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Savable.class)) {

                Savable fieldAnnotation = field.getAnnotation(Savable.class);
                if (fieldAnnotation.loadBehavior() == LoadBehavior.IGNORE || fieldAnnotation.saveBehavior() == SaveBehavior.IGNORE)
                    continue;

                if (fieldAnnotation.saveBehavior() == SaveBehavior.DO_NOTHING)
                    continue;

                try {
                    String name = fieldAnnotation.elementName();
                    if (Objects.equals(name, ""))
                        name = field.getName();

                    field.setAccessible(true);
                    registry.put(name, field.get(current));

                    System.out.println(registry);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
        try {
            jsonUtil.saveVariablesToJSON(registry, destinationFile);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * Set all the variables annotated with @Savable to their variable saved in the file
     */
    public void loadSavables() {
        for (Field field : currentClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Savable.class)) {

                Savable fieldAnnotation = field.getAnnotation(Savable.class);
                if (fieldAnnotation.loadBehavior() == LoadBehavior.IGNORE || fieldAnnotation.saveBehavior() == SaveBehavior.IGNORE)
                    continue;

                if (fieldAnnotation.loadBehavior() == LoadBehavior.KEEP)
                    continue;

                try {
                    String name = fieldAnnotation.elementName();
                    if (Objects.equals(name, ""))
                        name = field.getName();

                    Object value = registry.get(name);

                    if (fieldAnnotation.loadBehavior() == LoadBehavior.OVERRIDE_SAFE && value == null)
                        continue;

                    field.setAccessible(true);
                    field.set(current, value);

                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Get a specific object from the directory
     *
     * @param key The name of the variable wanted
     * @return The object associated to the key in the JSON file
     * @throws IOException Throw if the file is not found
     */
    public Object getSaved(String key) throws IOException {
        return jsonUtil.getVariableFromJSON(this.destinationFile, key);
    }

    /**
     * Save a specific object in the json
     *
     * @param key   Name of the object to save
     * @param value Object to save
     * @deprecated Use instead @Savable and saveSavables()
     */
    public void save(String key, Object value) {
        registry.put(key, value);
    }

    /**
     * Get the alternative path (even if it is not used)
     *
     * @return Return the alternative path
     */
    public String getAlternativePath() {
        return JSONUtility.alternativePath;
    }

    /**
     * Change the alternative path
     * WILL NOT CHANGE THE USE OF THE ALTERNATIVE PATH
     *
     * @param path New alternative path
     */
    public void setAlternativePath(String path) {
        JSONUtility.alternativePath = path;
    }

    /**
     * Get the state of use of the alternative path
     *
     * @return true if the alternative path is used, else false
     */
    public boolean isUsingAlternativePath() {
        return JSONUtility.useAlternativePath;
    }

    /**
     * Set the use of the alternative path to true
     */
    public void useAlternativePath() {
        JSONUtility.useAlternativePath = true;
    }

    /**
     * Set the use of the alternative path to false
     */
    public void dontUseAlternativePath() {
        JSONUtility.useAlternativePath = false;
    }

}
