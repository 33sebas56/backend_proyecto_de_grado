package com.ucc.convenios.approvals.service;

import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.repository.RoleRepository;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.exceptions.BadRequestException;
import com.ucc.convenios.shared.exceptions.ResourceNotFoundException;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import com.ucc.convenios.users.repository.ReviewerProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class ApprovalAssignmentService {

    private final RoleRepository roleRepository;
    private final ReviewerProfileRepository reviewerProfileRepository;

    public ApprovalAssignmentService(
            RoleRepository roleRepository,
            ReviewerProfileRepository reviewerProfileRepository
    ) {
        this.roleRepository = roleRepository;
        this.reviewerProfileRepository = reviewerProfileRepository;
    }

    public User assignReviewerForStage(ConvenioStage stage) {
        String roleName = getRoleNameForStage(stage);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado para la etapa " + stage.name()));

        List<ReviewerProfile> availableReviewers = reviewerProfileRepository.findByRoleAndAvailableTrue(role)
                .stream()
                .filter(profile -> profile.getCurrentActiveCases() < profile.getMaxActiveCases())
                .sorted(Comparator.comparing(ReviewerProfile::getCurrentActiveCases))
                .toList();

        if (availableReviewers.isEmpty()) {
            throw new BadRequestException("No hay revisores disponibles para la etapa " + stage.name());
        }

        ReviewerProfile selectedProfile = availableReviewers.get(0);
        selectedProfile.setCurrentActiveCases(selectedProfile.getCurrentActiveCases() + 1);
        reviewerProfileRepository.save(selectedProfile);

        return selectedProfile.getUser();
    }

    public void releaseReviewerCase(User user, ConvenioStage stage) {
        String roleName = getRoleNameForStage(stage);

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado para la etapa " + stage.name()));

        List<ReviewerProfile> profiles = reviewerProfileRepository.findByRoleAndAvailableTrue(role)
                .stream()
                .filter(profile -> profile.getUser().getId().equals(user.getId()))
                .toList();

        if (profiles.isEmpty()) {
            return;
        }

        ReviewerProfile profile = profiles.get(0);

        if (profile.getCurrentActiveCases() > 0) {
            profile.setCurrentActiveCases(profile.getCurrentActiveCases() - 1);
            reviewerProfileRepository.save(profile);
        }
    }

    private String getRoleNameForStage(ConvenioStage stage) {
        return switch (stage) {
            case PROYECCION -> "GESTOR_PROYECCION";
            case JURIDICA -> "REVISOR_JURIDICO";
            case FINANCIERA -> "REVISOR_FINANCIERO";
            case RECTORIA -> "RECTORIA";
        };
    }
}