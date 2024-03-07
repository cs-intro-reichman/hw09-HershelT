/** A linked list of character data objects.
 *  (Actually, a list of Node objects, each holding a reference to a character data object.
 *  However, users of this class are not aware of the Node objects. As far as they are concerned,
 *  the class represents a list of CharData objects. Likwise, the API of the class does not
 *  mention the existence of the Node objects). */
public class List {
    private Node first;

    // The number of elements in this list
    private int size;
	
    /** Constructs an empty list. */
    public List() {
        first = null;
        size = 0;
    }

    /** Returns the number of elements in this list. */
    public int getSize() {
 	      return size;
    }

    public Node getFirstNode() {
        return this.first;
    }
    /** Returns the first element in the list */
    public CharData getFirst() {
        return first.cp;
    }

    /** GIVE Adds a CharData object with the given character to the beginning of this list. */
    public void addFirst(char chr) {
        // Your code goes here
        Node newNode = new Node(new CharData(chr));
        newNode.next =  this.first;
        this.first = newNode;
        this.size++;
    }
    
    /** GIVE Textual representation of this list. */
    public String toString() {
        // Your code goes here
        if (this.size == 0) {
            return "()";
        }
        String s = "(";
        Node current = first;
        while (current != null) {
            s += current.cp.toString() + " ";
            current = current.next;
        }
        s = s.substring(0, s.length() - 1) + ")"; // remove the last space
        return s;
    }

    /** Returns the index of the first CharData object in this list
     *  that has the same chr value as the given char,
     *  or -1 if there is no such object in this list. */
    public int indexOf(char chr) {
        // Your code goes here
        Node current = this.first;
        int i = 0;

        while (current != null) {
            if (current.cp.equals(chr)) {
                return i;
            }
            current = current.next;
            i++;
        }
        return -1;


    }

    /** If the given character exists in one of the CharData objects in this list,
     *  increments its counter. Otherwise, adds a new CharData object with the
     *  given chr to the beginning of this list. */
    public void update(char chr) {
        // Your code goes here
        if (this.indexOf(chr) != -1) {

            Node current = this.first;
            while (current != null) {
                if (current.cp.equals(chr)) {
                    current.cp.count++;
                    return;
                }
                current = current.next;
            }
        }
        else{
            this.addFirst(chr);
        }
        
    }

    /** GIVE If the given character exists in one of the CharData objects
     *  in this list, removes this CharData object from the list and returns
     *  true. Otherwise, returns false. */
    public boolean remove(char chr) {
        // Your code goes here
        if (first == null) {
            return false;
        }
        if (first.cp.chr == chr) {
            first = first.next;
            this.size--;
            return true;
        }
        Node current = first;
        while (current != null) {
            if (current.cp.equals(chr)) {
                //checks for null
                if (current.next == null) {
                    return false;
                }
                current.next = current.next.next;
                this.size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /** Returns the CharData object at the specified index in this list. 
     *  If the index is negative or is greater than the size of this list, 
     *  throws an IndexOutOfBoundsException. */
    public CharData get(int index) {
        // Your code goes here
        if (index < 0 || index > this.size) {
            throw new IndexOutOfBoundsException();
        }
        Node current = first;
        int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        return current.cp;
    }

    /** Returns an array of CharData objects, containing all the CharData objects in this list. */
    public CharData[] toArray() {
	    CharData[] arr = new CharData[size];
	    Node current = first;
	    int i = 0;
        while (current != null) {
    	    arr[i++]  = current.cp;
    	    current = current.next;
        }
        return arr;
    }

    /** Returns an iterator over the elements in this list, starting at the given index. */
    public ListIterator listIterator(int index) {
	    // If the list is empty, there is nothing to iterate   
	    if (size == 0) return null;
	    // Gets the element in position index of this list
	    Node current = first;
	    int i = 0;
        while (i < index) {
            current = current.next;
            i++;
        }
        // Returns an iterator that starts in that element
	    return new ListIterator(current);
    }
}
