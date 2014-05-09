package se.nyhren.android.swlc4.vo;

import java.io.Serializable;
import java.util.List;

public class SA implements Serializable {
	
	private static final long serialVersionUID = -417023379438625555L;
	private int id;
	private boolean found;
	private String href;
	private String artist;
	private String album;
	private String availability;
	public String getAvailability() {
		return availability;
	}
	public void setAvailability(String availability) {
		this.availability = availability;
	}
	private long created;
	private List<SA> news;
	
	public void setFound(boolean found) {
		this.found = found;
	}
	public boolean isFound() {
		return found;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public long getCreated() {
		return created;
	}
	public void setCreated(long created) {
		this.created = created;
	}
	public void setNews(List<SA> news) {
		this.news = news;
	}
	public List<SA> getNews() {
		return news;
	}


}
