package se.nyhren.android.swlc4.vo;


public class SAResponse {
	private SA sa = null;
	private Response r = null;
	public void setSA(SA sa) {
		this.sa = sa;
	}
	public SA getSA() {
		return sa;
	}
	public void setR(Response r) {
		this.r = r;
	}
	public Response getR() {
		return r;
	}
}
