package com.todo.snapshot.repository;

import com.todo.snapshot.entity.Snapshot;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnapShotRepository extends JpaRepository<Snapshot, Long> {
}