package ConfigParser;

import Interfaces.IConfigParser;
import Interfaces.IConfigParserFactory;
import Interfaces.IConfigParserModel;
import ConfigParser.Parsers.*;
import com.fasterxml.jackson.dataformat.yaml.YAMLParser;

import java.util.HashMap;
import java.util.Map;

public class ConfigParserFactory implements IConfigParserFactory {
    Map<String, IConfigParser> parsers = new HashMap<String, IConfigParser>();
    public ConfigParserFactory(){
        /**
         * Парсер конфигов пока используется перед инициализацией сервисов. Поэтому
         */
        parsers.put(IConfigParserModel.YAML, new YamlConfigParser());


    }

    public IConfigParser Parser(String configType)
    {
        return parsers.get(configType);
    }
}
