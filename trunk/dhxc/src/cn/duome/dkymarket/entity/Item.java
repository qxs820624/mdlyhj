package cn.duome.dkymarket.entity;

public class Item {
    String route;
    String title;
    String header;
    int id;
    
    public Item(String route , String title , String header , int id){
    	 this.route = route ;
    	 this.title = title ;
    	 this.header = header;
    	 this.id = id;
    }

	public String getRoute() {
		return route;
	}
	public void setRoute(String end) {
		this.route = end;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public String toString() {
		return "Item [route=" + route + ", date=" + title
				+ ", header=" + header + "]";
	}  
}
