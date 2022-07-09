package cz.topolik.hashcodecollisions;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tomas Polesovsky
 *
 * Find String.hashCode() collisions to a given string as a postfix to a chosen word
 */
public class StringHashCodeCollisionsPostfixator {

	public static void main(String[] args) {
		String originalString = "http://valid-server/url";
		String prefix = "http://localhost:9200/_search#";
		int depth = 10;

		if (args.length > 2) {
			originalString = args[0];
			prefix = args[1];
			depth = Integer.parseInt(args[2]);
		} else {
			System.out.println("Syntax: java StringHashCodeCollisionsPostfixator [originalString] [prefix] [depth >= 1]");
		}


		long time = System.currentTimeMillis();

		findCollision(prefix, originalString, depth);

		System.out.println("Time: " + (System.currentTimeMillis() - time));
	}


	private static void findCollision(String word, String originalWord, int depth) {
		int targetHashCode = originalWord.hashCode();

		System.out.println("Searching collisions for target: '" + word + "' (hashCode: " + word.hashCode() + ") with source: " + originalWord + " (hashcode: " + targetHashCode + ") into depth: " + depth);

		int currentHC = word.hashCode();

		List<Thread> threads = new ArrayList<>(depth);

		for (int i = 1; i <= depth; i++) {
			final int currentDepth = i;

			threads.add(
				new Thread(()->{
					LinkedList<Character> stack = new LinkedList<>();

					if (!compute(targetHashCode, currentHC, currentDepth, stack)) {
						System.out.println("Not found in depth " + currentDepth);
						return;
					}

					StringBuffer result = new StringBuffer();
					result.append(word);
					for (Character ch : stack) {
						result.append(ch);
					}

					String collidingWord = result.toString();

					StringBuffer sb = new StringBuffer();
					sb.append("Found in depth " + currentDepth + ": ");
					sb.append(collidingWord);
					sb.append(" hashCode(): ");
					sb.append(collidingWord.hashCode());
					System.out.println(sb.toString());

				}
			));
		}


		int availableProcessors = Runtime.getRuntime().availableProcessors();
		if (availableProcessors > 1) {
			availableProcessors--;
		}

		int pos = 0;
		while(pos < threads.size()) {
			int alive = 0;
			for (int i = 0; i < pos; i++) {
				alive += threads.get(i).isAlive() ? 1 : 0;
			}
			for (int i = 0; i < (availableProcessors - alive); i++) {
				if (pos < threads.size()) {
					threads.get(pos++).start();
				}
			}

			try {
				Thread.currentThread().sleep(10);
			} catch (InterruptedException e) {}
		}

		for (int i = 0; i < threads.size(); i++) {
			try {
				threads.get(i).join();
			} catch (InterruptedException e) {}
		}
	}

	private static boolean compute(int targetHashCode, int currentHashCode, int depth, LinkedList<Character> stack) {
		if (depth == 0) {
			return targetHashCode == currentHashCode;
		}

		// use 31 printable chars only
		for (int ch = 64; ch < 96; ch++) {
			int hash = currentHashCode*31 + ch;

			if (hash == targetHashCode) {
				stack.push(new Character((char) (ch&0xff)));
				return true;
			}

			boolean result = compute(targetHashCode, hash, depth-1, stack);

			if (result) {
				stack.push(new Character((char) (ch&0xff)));
				return true;
			}
		}

		return false;
	}
}
