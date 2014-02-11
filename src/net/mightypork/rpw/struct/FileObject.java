package net.mightypork.rpw.struct;


/**
 * Entry in objects index
 * 
 * @author MightyPork
 */
public class FileObject {

	public String hash;
	public int size;


	public FileObject() {

	}


	public FileObject(String hash, int size) {

		this.hash = hash;
		this.size = size;
	}


	@Override
	public String toString() {

		return "Obj[hash: " + hash + ", size: " + size + "]";
	}
}
