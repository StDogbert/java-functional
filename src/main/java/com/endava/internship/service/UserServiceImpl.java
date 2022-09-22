package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import com.endava.internship.service.UserService;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserServiceImpl implements UserService {

    @Override
    public List<String> getFirstNamesReverseSorted(List<User> users) {
        return users
                .stream()
                .sorted(Comparator.comparing(User::getFirstName).reversed())
                .map(User::getFirstName)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> sortByAgeDescAndNameAsc(final List<User> users) {
        return users
                .stream()
                .sorted(
                        Comparator
                                .comparingInt(User::getAge).reversed()
                                .thenComparing(User::getFirstName)
                )
                .collect(Collectors.toList());
    }

    @Override
    public List<Privilege> getAllDistinctPrivileges(final List<User> users) {
        return users
                .stream()
                .map(User::getPrivileges)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> getUpdateUserWithAgeHigherThan(final List<User> users, final int age) {
        return users
                .stream()
                .filter(user -> user.getAge() > age)
                .filter(user -> user.getPrivileges().stream().anyMatch(privilege -> privilege == Privilege.UPDATE))
                .findFirst();
    }

    @Override
    public Map<Integer, List<User>> groupByCountOfPrivileges(final List<User> users) {
        return users
                .stream()
                .collect(
                        Collectors.groupingBy(user -> user.getPrivileges().size())
                );
    }

    @Override
    public double getAverageAgeForUsers(final List<User> users) {
        return users
                .stream()
                .mapToDouble(User::getAge)
                .average()
//                .orElseThrow(NoSuchElementException::new);
//                .orElse(null);
                .orElse(-1); // TODO: Consider changing this
//        Return of -1 for an undefined behaviour of empty array input variable seems to be described only in test.
//        Even if that is desired behaviour there are still multiple questions. Is this method internal, or
//        it provides data to some external API? If it is internal why do we need some magic return code
//        when we can and should use language syntax sugar for that by returning optional or throwing exception.
//        (and indicating that in method definition).
//        Even optional as a response here is un-optimal IMHO as this should be a simple method and leaving
//        responsibility of checking that input data is not empty to caller should lead to better readability.
//        Unless we want to start naming Holly Wars.
//        If this supposed to be a response to external API... Well, same arguments. Then lets increment API version
//        and introduce breaking changes.
//        Even if this was just a way to force tested person to inspect tests it is still very controversial.
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
//        More compact solution but seems a bit unreadable to me.
//        All conditions and requirements are mashed into one big line of code
//        return Optional.ofNullable(users
//                .stream()
//                .collect(Collectors.groupingBy(user -> user.getLastName(), Collectors.counting()))
//                .entrySet()
//                .stream()
//                .collect(Collectors.groupingBy(Map.Entry::getValue))
//                .entrySet()
//                .stream()
//                .max(Map.Entry.comparingByKey())
//                .map(Map.Entry::getValue)
//                .filter(v -> v.size() == 1)
//                .map(v -> v.get(0).getKey())
//                .orElse(null));

        if (users.isEmpty()) return Optional.empty();

        final Map<String, Long> lastNameFrequency = getNumberOfLastNames(users);
        final Long mostFrequentNamesCount = lastNameFrequency
                .entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> entry.getValue())
                .orElse(0L);

        // Last Name should be present at least two times
        if (mostFrequentNamesCount < 2) return Optional.empty();

        final Map<String, Long> filteredLastNameFrequency = lastNameFrequency
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() == mostFrequentNamesCount)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If more than one Last Name are the most frequent return optional empty
        if (filteredLastNameFrequency.size() != 1) return Optional.empty();

        return filteredLastNameFrequency
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .findFirst();
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        return users
                .stream()
                .filter(Arrays.stream(predicates).reduce(predicate -> true, Predicate::and))
                .collect(Collectors.toList());
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        return users
                .stream()
                .map(mapFun)
                .collect(Collectors.joining(delimiter));
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        return users
                .stream()
                .flatMap(
                        user -> user
                                .getPrivileges()
                                .stream()
                                .map(privilege -> new AbstractMap.SimpleEntry<>(privilege, user))
                )
                .collect(
                        Collectors
                                .groupingBy(
                                        Map.Entry::getKey,
                                        Collectors.mapping(Map.Entry::getValue, Collectors.toList())
                                )
                );
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        return users
                .stream()
                .collect(Collectors.groupingBy(user -> user.getLastName(), Collectors.counting()));
    }
}
