package com.admarkov.grishanya.radomlunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.random.RandomGenerator;

class VotesComparator implements Comparator<Option> {
    @Override
    public int compare(Option a, Option b) {
        return a.getVotes() < b.getVotes() ? 1 : a.getVotes() == b.getVotes() ? 0 : -1;
    }
}

public class RandomLunch {

    Option[] options;

    public RandomLunch() {
        options = new Option[]{
                new Option("Маркетплейс", 1, true),
                new Option("22см", 5, false),
                new Option("Каха", 5, true),
                new Option("Машита", 8, false),
                new Option("Чанг", 6, true),
                new Option("Калинка", 2, true),
        };
    }

    public Option[] getOptions() {
        Arrays.sort(options, new VotesComparator());
        return options;
    }

    public Option selectRandom() {
        return getOptions()[Math.abs(RandomGenerator.getDefault().nextInt() % getOptions().length)];
    }

    public Option selectTopByVotes() {
        return getOptions()[0];
    }

    public Option selectRandomWeightedWithVotes() {
        // убираем все варианты, за которые никто не проголосовал
        // и добавляем в список столько раз, сколько за вариант голосов
        ArrayList<Option> candidates = new ArrayList<Option>();
        for (Option option: getOptions()) {
            if (option.getVotes() > 0) {
                for (int i = 0 ; i < option.getVotes(); i++) {
                    candidates.add(option);
                }
            }
        }
        return candidates.get(Math.abs(RandomGenerator.getDefault().nextInt() % candidates.size()));
    }

    public void clearVotes() {
        for (Option option: options) {
            option.clearVotes();
        }
    }

}
