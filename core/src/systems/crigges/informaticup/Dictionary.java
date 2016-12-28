package systems.crigges.informaticup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import systems.crigges.informaticup.InputFileReader.Repository;

public class Dictionary {

	List<Repository> repositorys = null;
	ArrayList<String> words;
	Map<String, Integer> occurenceInRepos = new HashMap<String, Integer>();

	private final double intersectionThreshold = 0.9;

	public Dictionary(List<Repository> repositorys) {
		this.repositorys = repositorys;
		try {
			generateWords();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ArrayList<String> getWords() {
		return words;
	}

	private void generateWords() throws MalformedURLException, IOException {
		Map<String, Double>[] wordGroups = wordOccurences(genreateSetsAccordingToRepositoryType(repositorys));
		Map<String, Double> unification = generateUnficationMap(wordGroups);
		Map<String, Double>[] variance = calculateVarianceAll(unification, wordGroups);
		HashMap<String, Double> allwords = new HashMap<String, Double>();
		// for (Map<String, Double> typ : variance) {
		// allwords.putAll(typ);
		// }
		for (Map<String, Double> typ : variance) {
			allwords.putAll(typ);
		}
		Map<String, Double> sortedList = MapUtil.sortByValue(allwords);
		for (Entry<String, Double> entry : sortedList.entrySet()) {
			System.out.println(entry.getKey() + " " + entry.getValue());
		}
	}

	private Map<String, Integer>[] genreateSetsAccordingToRepositoryType(List<Repository> repositorys)
			throws MalformedURLException, IOException {
		Set<Entry<String, Integer>>[] wordGroups = (HashSet<Entry<String, Integer>>[]) new HashSet[7];
		Map<String, Integer>[] intersectionGroups = (HashMap<String, Integer>[]) new HashMap[7];
		Map<String, Integer>[] wordAppearance = (HashMap<String, Integer>[]) new HashMap[7];
		Map<String, Integer>[] wordRepetitions = (HashMap<String, Integer>[]) new HashMap[7];
		int[] repositoryCount = new int[7];
		for (int i = 0; i < wordGroups.length; i++) {
			wordGroups[i] = new HashSet<Entry<String, Integer>>();
			intersectionGroups[i] = new HashMap<String, Integer>();
			wordAppearance[i] = new HashMap<String, Integer>();
			wordRepetitions[i] = new HashMap<String, Integer>();
		}

		for (Repository t : repositorys) {
			GithubRepoCrawler crawler = RepoCacher.get(t.getName());
			Set<Entry<String, Integer>> words = crawler.getWordCount();
			System.out.println("before");
			for (Entry<String, Integer> word : words) {
				wordGroups[t.getTyp().getValue()].add(word);
			}
			System.out.println("after");
			repositoryCount[t.getTyp().getValue()]++;
		}
		System.out.println("finished");

		for (int i = 0; i < wordGroups.length; i++) {
			Set<Entry<String, Integer>> group = wordGroups[i];
			for (Entry<String, Integer> entry : group) {
				if (wordRepetitions[i].containsKey(entry.getKey())) {
					wordRepetitions[i].put(entry.getKey(), wordRepetitions[i].get(entry.getKey()) + 1);
					wordAppearance[i].put(entry.getKey(), entry.getValue() + wordAppearance[i].get(entry.getKey()));
				} else {
					wordRepetitions[i].put(entry.getKey(), 1);
					wordAppearance[i].put(entry.getKey(), entry.getValue());
				}
			}

			Iterator<Entry<String, Integer>> it = wordRepetitions[i].entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> word = it.next();
				if (repositoryCount[i] > 0
						&& ((double) (word.getValue()) / repositoryCount[i]) >= intersectionThreshold) {
					intersectionGroups[i].put(word.getKey(), wordAppearance[i].get(word.getKey()) - 1);
					occurenceInRepos.put(word.getKey(), word.getValue());

				}
			}
		}
		return intersectionGroups;
	}

	private Map<String, Double>[] wordOccurences(Map<String, Integer>[] list) {
		Map<String, Double>[] propabilityList = (HashMap<String, Double>[]) new HashMap[list.length];

		for (int i = 0; i < list.length; i++) {
			propabilityList[i] = new HashMap<String, Double>();
			int wordCount = 0;
			Iterator<Entry<String, Integer>> it1 = list[i].entrySet().iterator();
			while (it1.hasNext()) {
				wordCount += it1.next().getValue();
			}
			Iterator<Entry<String, Integer>> it = list[i].entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Integer> word = (Entry<String, Integer>) it.next();
				double occurence = (double) (word.getValue()) / wordCount;
				if (occurence > 0) {
					propabilityList[i].put(word.getKey(), occurence);
				}
			}
		}
		return propabilityList;
	}

	/**
	 * Genereates a Map with String as Key as Value the average Occurence of the Word in the intersections.
	 * @param list
	 * @return
	 */
	private Map<String, Double> generateUnficationMap(Map<String, Double>[] list) {
		Map<String, Double> unification = new HashMap<String, Double>();
		for (Map<String, Double> type : list) {
			Iterator<Entry<String, Double>> it = type.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, Double> word = it.next();
				if (unification.containsKey(word.getKey())) {
					unification.put(word.getKey(), unification.get(word.getKey()) + word.getValue());
				} else {
					unification.put(word.getKey(), word.getValue());
				}
			}
		}
		Iterator<Entry<String, Double>> it = unification.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> word = it.next();
			word.setValue(word.getValue() / occurenceInRepos.get(word.getKey()));
		}
		return unification;
	}

	private Map<String, Double>[] calculateVarianceAll(Map<String, Double> unification,
			Map<String, Double>[] listTypes) {
		Iterator<Entry<String, Double>> it = unification.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, Double> uWord = it.next();
			for (Map<String, Double> type : listTypes) {
					if (type.containsKey(uWord.getKey())) {
						type.put(uWord.getKey(), type.get(uWord.getKey()) / uWord.getValue());
				}
			}
		}
		return listTypes;

	}

	public static void main(String[] args) {
		List<Repository> list = null;
		try {
			list = new InputFileReader(new File("assets\\Repositorys.txt")).getRepositorysAndTypes();
			;
		} catch (Exception e) {
			e.printStackTrace();
		}
		new Dictionary(list);
	}
}
