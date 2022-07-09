package cz.topolik.hashcodecollisions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Tomas Polesovsky
 *
 * Find string collisions by mutating source words keeping the string length
 */
public class StringHashCodeCollisionsMutator {

	public static void main(String[] args) {
		if (args.length > 1) {
			String word = args[0];
			int depth = Integer.parseInt(args[1]);
			findAllCollisions(word, depth);
			return;
		}
		if (args.length > 0) {
			String word = args[0];
			findAllCollisions(word, 2);
			return;
		}
		System.out.println("Syntax: java StringHashCodeCollisions [wordToFindCollisionsFor] [charsToMutate >= 2]");
		System.out.println("-------");

		findAllCollisions("xhGWjqM7bMOEprOM", 3);
	//	findAllCollisions("home", 2);
	//	findAllCollisions("control_panel", 4);
	//	findAllCollisions("manage", 2);
	}

	private static void findAllCollisions(String word, int depth){
		findCollision(word, depth, 0);
	}

	private static void findCollision(String word, int depth, int fromPosition){
		long time = System.currentTimeMillis();
		System.out.println("Searching collisions for: '" + word + "' (hashCode: " + word.hashCode() + ") into depth: " + depth);

		List<Character> safeURLAlphabet = new ArrayList<>();
		for (char i = '0'; i <= '9'; i++) {
			safeURLAlphabet.add(i);
		}
		for (char i = 'a'; i <= 'z'; i++) {
			safeURLAlphabet.add(i);
		}
		safeURLAlphabet.add('-');
		safeURLAlphabet.add('_');
		safeURLAlphabet.add('.');

		if (depth >= word.length()) {
			depth = word.length();
			System.out.println("Performing exhaustive scan!");
		}

		int maxDepth = (int) (Math.log(Double.MAX_VALUE)/Math.log(safeURLAlphabet.size()));
		if(depth > maxDepth) {
			throw new IllegalArgumentException("Too deep! ("+depth+"). Max depth is " + maxDepth);
		}

		double computedCombinations = 0;
		for (int i = 2; i <= depth ; i++) {
			computedCombinations += Math.pow(safeURLAlphabet.size(), i);
		}

		System.out.println("Total combinations to be tried: " + computedCombinations);

		List<Thread> threads = new ArrayList<>();

		final int alphabetSize = safeURLAlphabet.size();

		final List<char[]>[] combinations = new ArrayList[word.length()];
		for (int i = 0; i < combinations.length; i++) {
			combinations[i] = new ArrayList<>();
		}

		final char[] wordChars = word.toCharArray();
		final List<Character> alphabet = safeURLAlphabet;

		for (int depthCounter = 2; depthCounter <= depth; depthCounter++) {
			final int fdepth = depthCounter;
			final double maxCombinations = Math.pow(alphabetSize, fdepth);

			for (int i = 0; i < word.length() - (fdepth - 1); i++) {
				final int pos = i;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						HashSet<char[]> result = new HashSet<>();
						char[] wordCharsToFindCollisionFor = new char[fdepth];
						System.arraycopy(wordChars, pos, wordCharsToFindCollisionFor, 0, fdepth);

						int hash = wordCharsToFindCollisionFor[0];
						for (int i = 1; i < fdepth; i++) {
							hash = hash * 31 + wordCharsToFindCollisionFor[i];
						}

						double same = 0;
						for (int i = 0; i < fdepth; i++) {
							same = same * alphabetSize + alphabet.indexOf(wordCharsToFindCollisionFor[i]);
						}

						for (double i = 0; i < maxCombinations; i++) {
							if (i == same) {
								continue;
							}

							char[] collisionChars = new char[fdepth];
							for (int j = 0; j < fdepth; j++) {
								int idx = (int) ((i / Math.pow(alphabetSize, j)) % alphabetSize);
								collisionChars[fdepth - j - 1] = alphabet.get(idx);
							}

							int collisionHash = collisionChars[0];
							for (int k = 1; k < fdepth; k++) {
								collisionHash = collisionHash * 31 + collisionChars[k];
							}

							if (collisionHash == hash) {
								result.add(collisionChars);
							}
						}

						synchronized (combinations[pos]) {
							combinations[pos].addAll(result);
						}

						StringBuffer sb = new StringBuffer();
						sb.append("Found " + result.size() + " collisions for position " + pos);
						if (result.size() > 0) {
							sb.append(" (");
							sb.append(wordCharsToFindCollisionFor);
							sb.append("): ");
							for (char[] combination : result) {
								sb.append(combination);
								sb.append(" ");
							}
						}

						System.out.println(sb.toString());
					}
				});

				thread.setDaemon(true);
				threads.add(thread);
			}
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

		char[] generated = word.toCharArray();
		Set<String> collisions = new TreeSet<>(generateCollisions(combinations, fromPosition, generated));
		System.out.println("Collisions: " + collisions);
		System.out.println("Found " + collisions.size() + " collisions in " + (System.currentTimeMillis() - time) + " ms");
		System.out.println("-------");

	}

	private static List<String> generateCollisions(List<char[]>[] combinations, int pos, char[] generated) {
		ArrayList<String> result = new ArrayList<>();
		if (pos >= combinations.length) {
			result.add(new String(generated));
			return result;
		}

		result.addAll(generateCollisions(combinations, pos + 1, generated));

		if (combinations[pos] != null)
		for(char[] combination : combinations[pos]) {
			char[] newGenerated = new char[generated.length];
			System.arraycopy(generated, 0, newGenerated, 0, generated.length);

			for (int i = 0; i < combination.length; i++) {
				newGenerated[pos + i] = combination[i];
			}

			result.addAll(generateCollisions(combinations, pos + combination.length, newGenerated));
		}

		return result;
	}

}
