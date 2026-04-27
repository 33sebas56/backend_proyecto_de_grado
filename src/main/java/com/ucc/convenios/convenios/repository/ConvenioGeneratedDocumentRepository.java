package com.ucc.convenios.convenios.repository;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioGeneratedDocument;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConvenioGeneratedDocumentRepository extends JpaRepository<ConvenioGeneratedDocument, UUID> {

    @EntityGraph(attributePaths = {"convenio", "convenioVersion", "approvalStep", "generatedBy"})
    List<ConvenioGeneratedDocument> findByConvenioOrderByGeneratedAtDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "convenioVersion", "approvalStep", "generatedBy"})
    Optional<ConvenioGeneratedDocument> findWithDetailsById(UUID id);
}