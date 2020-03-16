package org.corant.suites.jpa.hibernate.type;

import java.time.LocalDate;
import org.corant.suites.jpa.hibernate.type.descriptor.LocalDateGridTypeDescriptor;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.Mapping;
import org.hibernate.ogm.type.impl.AbstractGenericBasicType;
import org.hibernate.type.descriptor.java.LocalDateJavaDescriptor;

/**
 * corant-suites-jpa-hibernate
 *
 * @author bingo 下午12:52:40
 *
 */
public class LocalDateType extends AbstractGenericBasicType<LocalDate> {

  private static final long serialVersionUID = -3820937201712074842L;

  public static final LocalDateType INSTANCE = new LocalDateType();

  public LocalDateType() {
    super(LocalDateGridTypeDescriptor.INSTANCE, LocalDateJavaDescriptor.INSTANCE);
  }

  @Override
  public int getColumnSpan(Mapping mapping) throws MappingException {
    return 1;
  }

  @Override
  public String getName() {
    return null;
  }
}
