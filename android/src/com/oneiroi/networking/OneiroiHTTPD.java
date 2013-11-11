package com.oneiroi.networking;


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
		Response onCommandHistoryRequest(IHTTPSession session);
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
		if (session.getUri().equals("/history")) {
			response = this.provider.onCommandHistoryRequest(session);
		}
		else if (session.getUri().equals("/camera")) {
			response = this.provider.onCameraRequest(session);
		}
		else {
			String body = "<!DOCTYPE html><html><head><title>Robot Eyes</title><meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"></head><body style=\"padding: 40px;\"><div><div><iframe style=\"width: 40%; float: right; border: 0;\" src=\"history\"></iframe><div></div><div><img style=\"width: 60%; \" src=\"camera\"></img></div></body></html>";
			response = new Response(body);
		}
		
		return response;
	}

}

