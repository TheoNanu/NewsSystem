package newssystem;

public class News {
	private String author;
	private String publicationTime;
	private String lastEdit;
	private String source;
	private String content;
	private String category;
	
	public News(String author, String publicationTime, String lastEdit, String source, String category) {
		this.author = author;
		this.publicationTime = publicationTime;
		this.lastEdit = lastEdit;
		this.source = source;
		this.category = category;
	}
	
	public void setArticleContent(String content) {
		this.content = content;
	}
	
	public String getArticleContent() {
		return this.content;
	}
	
	public String getCategory() {
		return this.category;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublicationTime() {
		return publicationTime;
	}

	public void setPublicationTime(String publicationTime) {
		this.publicationTime = publicationTime;
	}

	public String getLastEdit() {
		return lastEdit;
	}

	public void setLastEdit(String lastEdit) {
		this.lastEdit = lastEdit;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	public String getRoutingKey() {
		return this.author + "." + this.category + "." + this.lastEdit +
				"." + this.publicationTime + "." + this.source;
	}
	
	public String toString() {
		return "Category: " + this.category + ", Author: " + this.author + ", Source: "
				+ this.source + ", Publication time: " + this.publicationTime + ", Last edit: "
				+ this.lastEdit + ", Content: " + this.content;
	}
}
