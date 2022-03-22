package org.miip.waterway.ui.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class MapFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {
        if (isForbidden(request))
                return;
        else
            chain.doFilter(request, response);
    }

	@Override
	public void destroy() {
		// NOTHING
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		//NOTHING
	}

	private boolean isForbidden( ServletRequest req ){
		return !req.getLocalAddr().equals(req.getRemoteAddr());
	}
}
