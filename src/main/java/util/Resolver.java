package util;

import io.bretty.solver.normalization.*;

import java.util.Set;

public class Resolver {
    public static void main(String[] args) {
        Set<Attribute> attrs = Attribute.getSet("employee_id, employee_bonus_ratio, teacher_id, manager_id, course_id, mandatory_status, department_id, exam_grade, exam_date, make_up_exam_app_date, make_up_exam_app_approval_status, make_up_exam_app_fee, make_up_exam_grade");
        Set<FuncDep> fds = FuncDep.getSet("manager_id --> department_id; department_id --> manager_id; course_id --> teacher_id; employee_id, course_id --> exam_grade, exam_date; course_id, department_id --> mandatory_status; employee_id --> department_id; exam_date, make_up_exam_app_date --> make_up_exam_app_fee");
        Relation relation = new Relation(attrs, fds);
        Set<Relation> r3nf = relation.decomposeTo3NF();
        for (Relation r:
                r3nf) {
            System.out.println(r);
            System.out.println();
        }

//        System.out.println("\n");
//
//        Set<Relation> rbcnf = relation.decomposeToBCNF();
//        for (Relation r:
//                rbcnf) {
//            System.out.println(r);
//            System.out.println();
//        }
    }
}
