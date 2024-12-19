package com.weavusys.hrd.controller;

import com.weavusys.hrd.calCulBatch.BatchAccrualService;
import com.weavusys.hrd.entity.Accrual;
import com.weavusys.hrd.entity.Employee;
import com.weavusys.hrd.repo.EmployeeRepository;
import com.weavusys.hrd.service.AccrualService;
import com.weavusys.hrd.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Tag(name = "1.Employee", description = "Employee management API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;
    private final AccrualService accrualService;
    private final EmployeeService employeeService;

    private final BatchAccrualService batchAccrualService;

    @Operation(summary = "직원리스트 조회", description = "모든 직원을 조회합니다.")
    @GetMapping("/lists")
    public List<Employee> getEmployeeList() {
        return employeeService.findAll();
    }

    @Operation(summary = "직원정보 조회", description = "직원의 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직원을 조회하였습니다."),
            @ApiResponse(responseCode = "404", description = "직원이 존재하지 않습니다.")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployee(@Parameter(description = "직원의 ID") @PathVariable String id) {
        Optional<Employee> employee = employeeRepository.findById(id);
        return employee.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

//    @Operation(summary = "적립리스트 내역 조회", description = "모든직원의 적립내역을 조회합니다.")
//    @GetMapping("/accruals")
//    public List<Accrual> getAllEmployees() {
//        return accrualService.findAll();
//    }

    @Operation(summary = "직원 등록", description = "새로운 직원을 등록합니다.")
    @PostMapping
    public String addEmployee(@RequestBody Employee employee) {
        if (employeeService.save(employee)) {
            return "등록이 완료되었습니다.";
        }
        return "다시 한번 입력해 주세요.";
    }

//    @Operation(summary = "특정 직원의 적립 내역 조회", description = "특정 직원의 적립 내역을 조회합니다.")
//    @GetMapping("/{id}/accrual")
//    public String getAccrual(@Parameter(description = "직원의 ID") @PathVariable String id) {
//        Accrual accrual = accrualService.findByEmployeeId(id).orElseThrow();
//        String result = String.valueOf(accrualService.calculateTotalAccrual(accrual));
//        return result;
//    }

    @Operation(summary = "직원 정보 수정", description = "특정 직원의 정보를 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<Employee> modifyEmployee(@Parameter(description = "직원의 ID") @PathVariable String id, @RequestBody Employee employee) {
        Employee modifyEmployee = employeeService.modifyEmployee(id, employee);
        return modifyEmployee != null ? ResponseEntity.ok(modifyEmployee) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "직원 삭제", description = "특정 직원을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직원이 삭제되었습니다."),
            @ApiResponse(responseCode = "404", description = "직원이 존재하지 않습니다.")
    })
    @PostMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@Parameter(description = "직원의 ID") @PathVariable String id) {
        boolean isDeleted = employeeService.deleteById(id);
        return isDeleted ? ResponseEntity.ok("직원 삭제가 완료되었습니다.") : ResponseEntity.notFound().build();
    }

    @GetMapping ("/accruals/yearly-total/{year}")
    public long getYearlyAccrualTotal(@PathVariable int year) {
        return batchAccrualService.calculateYearlyAccrualTotal(year);
    }
}