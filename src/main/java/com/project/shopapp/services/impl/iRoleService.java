package com.project.shopapp.services.impl;

import com.project.shopapp.models.Role;
import com.project.shopapp.repository.RoleRepository;
import com.project.shopapp.services.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class iRoleService implements RoleService {
    @Autowired
    private RoleRepository roleRepository;
    @Override
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}
