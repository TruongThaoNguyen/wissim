package parser;
public class replaceTest {
	public static void main(String args[]) {
		String a = "a  b  c d";
		a.replace("  ", " ");
		String after = a.trim().replaceAll(" +", " ");
		System.out.println(after);
	}
}
