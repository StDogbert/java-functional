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
            .orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
//        More stream oriented solution but seems a bit unreadable to me.
//        All conditions and requirements are mashed into one big line of code
//        return Optional.ofNullable(
//                getNumberOfLastNames(users)
//                .entrySet()
//                .stream()
//                .collect(Collectors.groupingBy(Map.Entry::getValue))
//                .entrySet()
//                .stream()
//                .max(Map.Entry.comparingByKey())
//                .map(Map.Entry::getValue)
//                .filter(list -> list.size() == 1) // Only one frequent last name makes response valid
//                .map(list -> list.get(0))
//                .filter(entry -> entry.getValue() > 1) // Last Name should be present at least two times
//                .map(entry -> entry.getKey())
//                .orElse(null)
//               );

        if (users.isEmpty()) {
            return Optional.empty();
        }

        final Map<String, Long> lastNameFrequency = getNumberOfLastNames(users);
        final Long mostFrequentNamesCount = lastNameFrequency
            .entrySet()
            .stream()
            .max(Map.Entry.comparingByValue())
            .map(entry -> entry.getValue())
            .orElse(0L);

        // Last Name should be present at least two times
        if (mostFrequentNamesCount < 2) {
            return Optional.empty();
        }

        final Map<String, Long> filteredLastNameFrequency = lastNameFrequency
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() == mostFrequentNamesCount)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        // If more than one Last Name are the most frequent return optional empty
        if (filteredLastNameFrequency.size() != 1) {
            return Optional.empty();
        }

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
                    .map(privilege -> Map.entry(privilege, user))
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
