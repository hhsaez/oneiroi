package com.oneiroi.networking;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import android.util.Log;

public class OneiroiHTTPD extends NanoHTTPD {
	
	public static final String MIME_PLAINTEXT = "text/plain";
    public static final String MIME_HTML = "text/html";
	public static final String MIME_JS = "application/javascript";
	public static final String MIME_CSS = "text/css";
	public static final String MIME_PNG = "image/png";
	public static final String MIME_JPG = "image/jpeg";
	public static final String MIME_DEFAULT_BINARY = "application/octet-stream";
    public static final String MIME_XML = "text/xml";
	
	public interface Provider {
		Response onStatusRequest(IHTTPSession session);
		Response onCameraRequest(IHTTPSession session);
	}
	
	private Provider provider;
	
	public OneiroiHTTPD(Provider provider) {
		super(8080);
		
		this.provider = provider;
	}
	
	@Override
	public Response serve(IHTTPSession session) {
		Response response = null;
		if (session.getUri().equals("/status")) {
			response = this.provider.onStatusRequest(session);
		}
		else if (session.getUri().equals("/camera")) {
			response = this.provider.onCameraRequest(session);
		}
		else {
			StringBuilder sb = new StringBuilder();
	        sb.append("<html>");
	        sb.append("<head><title>Oneiroi Server</title></head>");
	        sb.append("<body>");
	        sb.append("<h1>Response</h1>");
	        sb.append("<p><blockquote><b>URI -</b> ").append(session.getUri()).append("<br />");
	        sb.append("<b>Method -</b> ").append(session.getMethod()).append("</blockquote></p>");
	        sb.append("<h3>Headers</h3><p><blockquote>").append(session.getHeaders()).append("</blockquote></p>");
	        sb.append("<h3>Parms</h3><p><blockquote>").append(session.getParms()).append("</blockquote></p>");
	        sb.append("<h3>Files</h3><p><blockquote>").append(session.getInputStream()).append("</blockquote></p>");
	        sb.append("</body>");
	        sb.append("</html>");
	        response =new Response(sb.toString());
		}
		
		return response;
	}

}

