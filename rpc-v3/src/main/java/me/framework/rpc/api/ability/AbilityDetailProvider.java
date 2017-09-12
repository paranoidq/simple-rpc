package me.framework.rpc.api.ability;

import me.framework.rpc.core.server.MessageRecvExecutor;
import me.framework.rpc.util.ReflectionUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author paranoidq
 * @since 1.0.0
 */
public class AbilityDetailProvider implements AbilityDetail {

    private final static String STYLE = "<style type=\"text/css\">\n" +
        "table.gridtable {\n" +
        "    font-family: verdana,arial,sans-serif;\n" +
        "    font-size:11px;\n" +
        "    color:#333333;\n" +
        "    border-width: 1px;\n" +
        "    border-color: #666666;\n" +
        "    border-collapse: collapse;\n" +
        "}\n" +
        "table.gridtable th {\n" +
        "    border-width: 1px;\n" +
        "    padding: 8px;\n" +
        "    border-style: solid;\n" +
        "    border-color: #666666;\n" +
        "    background-color: #dedede;\n" +
        "}\n" +
        "table.gridtable td {\n" +
        "    border-width: 1px;\n" +
        "    padding: 8px;\n" +
        "    border-style: solid;\n" +
        "    border-color: #666666;\n" +
        "    background-color: #ffffff;\n" +
        "}\n" +
        "</style>";

    private final static String HEADER = "<table class=\"gridtable\">\n" +
        "<tr>\n" +
        "    <th>NettyRPC Ability Detail</th>\n" +
        "</tr>";

    private final static String TAIL = "</table>";
    private final static String CELL_BEGIN = "<tr><td>";
    private final static String CELL_END = "</td></tr>";

    public StringBuilder listAbilityDetail(boolean html) {
        Map<String, Object> map = MessageRecvExecutor.getInstance().getHandlerMap();
        ReflectionUtils utils = new ReflectionUtils();

        if (html) {
            utils.getProvider().append(STYLE).append(HEADER);
        }

        Set<String> s = (Set<String>) map.keySet();
        Iterator<String> iterator = s.iterator();
        String key;
        while (iterator.hasNext()) {
            key = iterator.next();
            try {
                if (html) {
                    utils.getProvider().append(CELL_BEGIN);
                    utils.listRpcProviderDetail(Class.forName(key), html);
                    utils.getProvider().append(CELL_END);
                } else {
                    utils.listRpcProviderDetail(Class.forName(key), html);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        if (html) {
            utils.getProvider().append(TAIL);
        }

        return utils.getProvider();

    }
}
