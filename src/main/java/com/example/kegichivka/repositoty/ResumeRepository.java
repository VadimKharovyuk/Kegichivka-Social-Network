package com.example.kegichivka.repositoty;

import com.example.kegichivka.model.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository  extends JpaRepository<Resume, Long> {
    List<Resume> findByUserId(Long id);

}
