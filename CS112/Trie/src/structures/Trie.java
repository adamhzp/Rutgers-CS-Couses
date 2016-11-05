package structures;

import java.util.ArrayList;

/**
 * This class implements a compressed trie. Each node of the tree is a CompressedTrieNode, with fields for
 * indexes, first child and sibling.
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	/**
	 * Words indexed by this trie.
	 */
	ArrayList<String> words;
	
	/**
	 * Root node of this trie.
	 */
	TrieNode root;
	
	/**
	 * Initializes a compressed trie with words to be indexed, and root node set to
	 * null fields.
	 * 
	 * @param words
	 */
	public Trie() {
		root = new TrieNode(null, null, null);
		words = new ArrayList<String>();
	}
	
	/**
	 * Inserts a word into this trie. Converts to lower case before adding.
	 * The word is first added to the words array list, then inserted into the trie.
	 * 
	 * @param word Word to be inserted.
	 */
	public void insertWord(String word) {
		
		if(root.firstChild == null){
			words.add(word);
			root.firstChild = new TrieNode(new Indexes(0,(short)0,(short)(word.length()-1)),null,null);
			return;
		}
		
		
		int index = words.size();
		words.add(word);
		this.insert(word, root, "", index);
		
	}
	
	private void insert(String word, TrieNode t,String prefix, int index){
		TrieNode curr = t.firstChild;
		if(curr == null)
		{
			t.firstChild = new TrieNode(new Indexes(index,(short)(t.substr.endIndex+1),(short)(word.length()-1)),null,null);
			return;
		}

		TrieNode prev = t;

		while(curr!=null){
			String s1 = prefix+words.get(curr.substr.wordIndex).substring(curr.substr.startIndex,curr.substr.endIndex+1);
			int i = compare(s1, word);

			if(i != -1 && i==curr.substr.endIndex){
				insert(word,curr, s1,index);
				return;

			}else if(i!= -1 && i<curr.substr.endIndex){
				String s = word.substring(0,i+1);
				if(prefix.indexOf(s)==-1){
					TrieNode temp = new TrieNode(new Indexes(curr.substr.wordIndex,(short)(i+1),curr.substr.endIndex),curr.firstChild,null);
					curr.substr = new Indexes(curr.substr.wordIndex,curr.substr.startIndex,(short)i);
					curr.firstChild = temp;
					temp.sibling = new TrieNode(new Indexes(index,(short)(i+1),(short)(word.length()-1)), null,null);
					return;
				}
				
				
				
			}
			prev = curr;
			curr = curr.sibling;
			
		}
		
		if(t!= root){
			prev.sibling = new TrieNode(new Indexes(index,(short)(t.substr.endIndex+1),(short)(word.length()-1)),null,null);
		}else { 
			prev.sibling = new TrieNode(new Indexes(index,(short)0,(short)(word.length()-1)),null,null);
		}
		
	}
	
		private int compare(String s1, String s2){
			System.out.println(s1+" vs "+ s2);
		int k=s1.length();
		int out=-1;
		if(s1.length()>s2.length()){
			k=s2.length();
			}		
		for(int a=0; a<k;a++){
			if(s1.charAt(a)==s2.charAt(a)){				
				out++;				
			}
			else{
				break;
			}
		}
		System.out.println(out);
		return out;		
	}
	
	
	
	/**
	 * Given a string prefix, returns its "completion list", i.e. all the words in the trie
	 * that start with this prefix. For instance, if the tree had the words bear, bull, stock, and bell,
	 * the completion list for prefix "b" would be bear, bull, and bell; for prefix "be" would be
	 * bear and bell; and for prefix "bell" would be bell. (The last example shows that a prefix can be
	 * an entire word.) The order of returned words DOES NOT MATTER. So, if the list contains bear and
	 * bell, the returned list can be either [bear,bell] or [bell,bear]
	 * 
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all words in tree that start with the prefix, order of words in list does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public ArrayList<String> completionList(String prefix) {
		ArrayList<String> result = new ArrayList<String>();
		for(int a=0; a<words.size();a++){
			if(words.get(a).startsWith(prefix)){
				result.add(words.get(a));
			}
		}
		return result;
	}
	
	
	public void print() {
		print(root, 1, words);
	}
	
	private static void print(TrieNode root, int indent, ArrayList<String> words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			System.out.println("      " + words.get(root.substr.wordIndex));
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		System.out.println("(" + root.substr + ")");
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
