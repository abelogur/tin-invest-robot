package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.repository.CandleRepository;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

@Repository
public class CandleInMemoryRepository implements CandleRepository {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final Supplier<TreeSet<CachedCandle>> EMPTY_TREE_SET = () -> new TreeSet<>(
            Comparator.comparing(CachedCandle::getTime)
    );

    private final HashMap<CandleGroupId, TreeSet<CachedCandle>> candles = new HashMap<>();


    @Override
    public boolean add(CandleGroupId groupId, CachedCandle candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(groupId, EMPTY_TREE_SET.get());
            return candles.get(groupId).add(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(CandleGroupId groupId, Collection<CachedCandle> candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(groupId, EMPTY_TREE_SET.get());
            candles.get(groupId).addAll(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public SortedSet<CachedCandle> getAll(CandleGroupId groupId) {
        try {
            lock.readLock().lock();
            return candles.getOrDefault(groupId, EMPTY_TREE_SET.get());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public void remove(CandleGroupId groupId) {
        try {
            lock.writeLock().lock();
            candles.remove(groupId);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
