
public class Value {
	private boolean isNumeric;
	private double numericValue;
	private String categoricalValue;
	
	public Value(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}
	
	public Value setValue(Object input){
		if(isNumeric){
			numericValue=(double)input;
		}
		else
		{
			categoricalValue=(String)input;
		}
		return this;
	}
	
	public Object getValue(){
		return isNumeric?numericValue:categoricalValue;
	}
	
	public boolean isNumeric() {
		return isNumeric;
	}
	
	@Override
	public String toString() {
		String output="";
		return output = isNumeric?"N:"+numericValue:"C:"+categoricalValue;
	}
}
