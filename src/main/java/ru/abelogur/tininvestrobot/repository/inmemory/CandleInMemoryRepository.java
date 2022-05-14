package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.domain.CandleGroupId;
import ru.abelogur.tininvestrobot.repository.CandleRepository;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class CandleInMemoryRepository implements CandleRepository {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final TreeSet<CachedCandle> EMPTY_TREE_SET = new TreeSet<>(
            Comparator.comparing(CachedCandle::getTime)
    );

    private final HashMap<CandleGroupId, TreeSet<CachedCandle>> candles = new HashMap<>();


    @Override
    public boolean add(CandleGroupId groupId, CachedCandle candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(groupId, EMPTY_TREE_SET);
            return candles.get(groupId).add(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(CandleGroupId groupId, Collection<CachedCandle> candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(groupId, EMPTY_TREE_SET);
            candles.get(groupId).addAll(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public SortedSet<CachedCandle> getAll(CandleGroupId groupId) {
        try {
            lock.readLock().lock();
            return candles.getOrDefault(groupId, EMPTY_TREE_SET);
        } finally {
            lock.readLock().unlock();
        }
    }
}
