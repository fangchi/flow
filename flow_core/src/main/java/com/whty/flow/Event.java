package com.whty.flow;

/**
 * 事件
 * @author andrey
 */
public class Event {

	private String val;
	
	public Event(String val) {
		super();
		this.val = val;
	}

	public String getVal() {
		return val;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((val == null) ? 0 : val.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Event other = (Event) obj;
		if (val == null) {
			if (other.val != null)
				return false;
		} else if (!val.equals(other.val))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Event [val=" + val + "]";
	}
}
