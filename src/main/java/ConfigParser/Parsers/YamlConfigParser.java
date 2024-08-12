package ConfigParser.Parsers;

import Interfaces.IConfigParser;
import Interfaces.IContainer;
import ServiceProvider.ServiceContainer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;
import org.apache.http.util.Args;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class YamlConfigParser implements IConfigParser {
    public void parse(URL configURL, IContainer container)
    {
        Args.notNull(configURL, "Config URL cannot be null");

        Map<String, Object> config = null;

        File configFile = new File(configURL.getFile());
        YAMLFactory yamlFactory = new YAMLFactory();

        try {

            YAMLParser yamlParser = yamlFactory.createParser(configFile);
            config = parseYamlFile(yamlParser);

            if (config.containsKey("parameters") && config.get("parameters") instanceof Map) {
                Map<String, Object> parameters = (Map<String, Object>) config.get("parameters");

                for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                    ((ServiceContainer)container).setParameter(entry.getKey(), entry.getValue());
                }
            }

        } catch (IOException e) {
            //TODO throw own exception
            throw new RuntimeException(e.getMessage());
        }
    }

    private static  Map<String, Object> parseYamlFile(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        //TODO split yaml parser to separate class
        return parseYamlFileObjectStructure(yamlParser);
    }

    private static List<Map<String, Object>> parseYamlFileArrayStructure(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        List<Map<String, Object>> list = new ArrayList<>();
        JsonToken jsonToken = yamlParser.nextToken();

        while (
                jsonToken != null &&
                        jsonToken != JsonToken.END_ARRAY
        ) {
            switch (jsonToken) {
                case START_OBJECT: System.out.println("Object Started");
                    list.add(parseYamlFileObjectStructure(yamlParser));
                    break;
                case END_OBJECT: System.out.println("Object Ended");
                    break;
                case START_ARRAY: System.out.println("Array Started");
                    break;
                case END_ARRAY: System.out.println("Array Ended");
                    break;
                case FIELD_NAME: System.out.println("Key field: " + yamlParser.getText());
                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                case VALUE_STRING:
                case VALUE_TRUE:
                default:System.out.println("Key value: " + yamlParser.getText());
                    break;
            }
            jsonToken = yamlParser.nextToken();
        }

        if (jsonToken != JsonToken.END_ARRAY) {
            throw new RuntimeException("Не найден завершающий токен ]");
        }

        return list;
    }

    private static  Map<String, Object> parseYamlFileObjectStructure(YAMLParser yamlParser) throws JsonParseException, JsonMappingException, IOException
    {
        return parseYamlFileObjectStructure(yamlParser, null);
    }

    private static  Map<String, Object> parseYamlFileObjectStructure(
            YAMLParser yamlParser,
            JsonToken beginWithToken
    ) throws JsonParseException, JsonMappingException, IOException
    {
        String fieldName;
        HashMap<String, Object> object = new HashMap<>();
        JsonToken jsonToken = beginWithToken != null ?
                beginWithToken :
                yamlParser.nextToken();
        JsonToken prevToken = null;
        String prevTokenId;
        Stack<String> fields = new Stack<>();

        while (
                jsonToken != null &&
                        jsonToken != JsonToken.END_OBJECT
        ) {
            switch (jsonToken) {
                case START_OBJECT:

                    Map<String, Object> map = parseYamlFileObjectStructure(yamlParser);
                    if (fields.size() == 0) {
                        return map;
//                        throw new RuntimeException("Неверная структура yaml-файла");
                    }
                    fieldName = fields.pop();
                    object.put(fieldName, map);
                    break;
                case START_ARRAY:
                    fieldName = fields.pop();
                    if (fieldName == null) {
                        throw new RuntimeException("Неверная структура yaml-файла");
                    }
                    object.put(fieldName, parseYamlFileArrayStructure(yamlParser));
                    break;
                case END_OBJECT:
                case END_ARRAY:
                    //TODO Сделать определение имени файла и номера строки
                    String message = "Неожиданный токен " + jsonToken + " в строке ";// + yamlParser.
                    throw new RuntimeException(message);
                case FIELD_NAME:
                    //TODO Возможно, что field, следующий за field - это тоже объект типа:
                    //   field:
                    //      field2:
                    //         field3: some_value

                    fields.push(yamlParser.getText());
                    if (prevToken == JsonToken.FIELD_NAME) {
                        object.put(fields.pop(), parseYamlFileObjectStructure(yamlParser, jsonToken));
                    }

                    break;
                case VALUE_FALSE:
                case VALUE_NULL:
                case VALUE_NUMBER_FLOAT:
                case VALUE_NUMBER_INT:
                case VALUE_STRING:
                case VALUE_TRUE:
                default:

                    //TODO Обрабатывать пустые строки, если таковые имеются
                    String value = yamlParser.getText();
                    if (
                            value == null ||
                                    value.trim() == "" ||
                                    value.trim() == "~" ||
                                    value.trim() == "null"
                    ) {
                        object.put(fields.pop(), null);
                    }
                    else {
                        switch (value.trim().toLowerCase()) {
                            case "true":
                                object.put(fields.pop(), true);
                                break;
                            case "false":
                                object.put(fields.pop(), false);
                                break;
                            default:
                                object.put(fields.pop(), yamlParser.getText());
                                break;
                        }
                    }

                    break;
            }
            prevToken = jsonToken;
            jsonToken = yamlParser.nextToken();
        }

        if (jsonToken != JsonToken.END_OBJECT)
        {
            throw new RuntimeException("Не найден завершающий токен }");
        }

        while (fields.size() > 0) {
            object.put(fields.pop(), null);
        }
        return object;
    }
}
