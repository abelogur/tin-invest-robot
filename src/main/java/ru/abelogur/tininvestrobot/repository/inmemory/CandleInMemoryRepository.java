package ru.abelogur.tininvestrobot.repository.inmemory;

import org.springframework.stereotype.Repository;
import ru.abelogur.tininvestrobot.domain.CachedCandle;
import ru.abelogur.tininvestrobot.repository.CandleRepository;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class CandleInMemoryRepository implements CandleRepository {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private static final TreeSet<CachedCandle> EMPTY_TREE_SET = new TreeSet<>(
            Comparator.comparing(CachedCandle::getTime)
    );

    private final HashMap<String, TreeSet<CachedCandle>> candles = new HashMap<>();


    @Override
    public boolean add(String figi, CachedCandle candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(figi, EMPTY_TREE_SET);
            return candles.get(figi).add(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public void addAll(String figi, Collection<CachedCandle> candle) {
        try {
            lock.writeLock().lock();
            candles.putIfAbsent(figi, EMPTY_TREE_SET);
            candles.get(figi).addAll(candle);
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Collection<CachedCandle> getAll(String figi) {
        try {
            lock.readLock().lock();
            candles.putIfAbsent(figi, EMPTY_TREE_SET);
            return candles.get(figi);
        } finally {
            lock.readLock().unlock();
        }
    }
}
