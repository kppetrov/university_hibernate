package ua.com.foxminded.university.dao.jpa;

import org.springframework.stereotype.Repository;

import ua.com.foxminded.university.dao.PeriodDao;
import ua.com.foxminded.university.model.Period;

@Repository("PeriodDaoImpl")
public class PeriodDaoImpl extends GenericDaoImpl<Period> implements PeriodDao {

    public PeriodDaoImpl() {
        super.setEntityClass(Period.class);
    }    
}
