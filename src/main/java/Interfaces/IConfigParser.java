package Interfaces;

import java.net.URL;
import java.util.Map;

public interface IConfigParser {
    void parse(URL configURL, IContainer container);
}
