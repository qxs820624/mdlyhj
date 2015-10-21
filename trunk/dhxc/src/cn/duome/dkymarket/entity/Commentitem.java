package cn.duome.dkymarket.entity;

public class Commentitem {
   public String comment ;
   public String date ;
   public String header ;
	
    public Commentitem(String comment , String date , String header) {
		// TODO Auto-generated constructor stub
    	this.comment = comment;
    	this.date = date;
    	this.header = header;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	@Override
	public String toString() {
		return "Commentitem [comment=" + comment + ", date=" + date
				+ ", header=" + header + "]";
	}
    
}
