package com.ibm.ram.guards.resourceserver.web.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.HashMap;
import java.util.Map;

/***
 * Request wrapper enabling the update of a request-parameter.
 *
 * @author E.K. de Lang
 *
 */
final class HttpServletRequestReplaceParameterWrapper
        extends HttpServletRequestWrapper
{

    private final Map<String, String[]> keyValues;

    @SuppressWarnings("unchecked")
    HttpServletRequestReplaceParameterWrapper(HttpServletRequest request, String key, String value)
    {
        super(request);

        keyValues = new HashMap<String, String[]>();
        keyValues.putAll(request.getParameterMap());
        // Can override the values in the request
        keyValues.put(key, new String[] { value });

    }

    @SuppressWarnings("unchecked")
    HttpServletRequestReplaceParameterWrapper(HttpServletRequest request, Map<String, String> additionalRequestParameters)
    {
        super(request);
        keyValues = new HashMap<String, String[]>();
        keyValues.putAll(request.getParameterMap());
        for (Map.Entry<String, String> entry : additionalRequestParameters.entrySet()) {
            keyValues.put(entry.getKey(), new String[] { entry.getValue() });
        }

    }

    @Override
    public String getParameter(String name)
    {
        if (keyValues.containsKey(name)) {
            String[] strings = keyValues.get(name);
            if (strings == null || strings.length == 0) {
                return null;
            }
            else {
                return strings[0];
            }
        }
        else {
            // Just in case the request has some tricks of it's own.
            return super.getParameter(name);
        }
    }

    @Override
    public String[] getParameterValues(String name)
    {
        String[] value = this.keyValues.get(name);
        if (value == null) {
            // Just in case the request has some tricks of it's own.
            return super.getParameterValues(name);
        }
        else {
            return value;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap()
    {
        return this.keyValues;
    }

}
