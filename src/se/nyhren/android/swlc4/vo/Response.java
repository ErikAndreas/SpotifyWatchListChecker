package se.nyhren.android.swlc4.vo;
public class Response {
	private int rc = 0;
	private String body = null;
	private String url = null;
	public void setRc(int rc) {
		this.rc = rc;
	}
	public int getRc() {
		return rc;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getBody() {
		return body;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
}
