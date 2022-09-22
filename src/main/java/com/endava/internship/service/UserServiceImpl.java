package com.endava.internship.service;

import com.endava.internship.domain.Privilege;
import com.endava.internship.domain.User;
import com.endava.internship.service.UserService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Comparator;
import java.util.Collection;
import java.util.NoSuchElementException;
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
                .orElse(-1);
    }

    @Override
    public Optional<String> getMostFrequentLastName(final List<User> users) {
        return Optional.ofNullable(users
                .stream()
                .collect(Collectors.groupingBy(user -> user.getLastName(), Collectors.counting()))
                .entrySet().stream()
                .collect(Collectors.groupingBy(Map.Entry::getValue))
                .entrySet().stream()
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .filter(v -> v.size() == 1)
                .map(v -> v.get(0).getKey())
                .orElse(null));
    }

    @Override
    public List<User> filterBy(final List<User> users, final Predicate<User>... predicates) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String convertTo(final List<User> users, final String delimiter, final Function<User, String> mapFun) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Map<Privilege, List<User>> groupByPrivileges(List<User> users) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Map<String, Long> getNumberOfLastNames(final List<User> users) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
