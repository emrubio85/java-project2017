package data;

public interface Iterator<E> {
	void reset();
	E next();
	E currentItem();
	boolean hasNext();
	
}
