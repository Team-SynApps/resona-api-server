package synapps.resona.api.socialMedia.report.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import synapps.resona.api.global.entity.BaseEntity;
import synapps.resona.api.member.entity.member.Member;

@Entity
@Getter
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "report_type")
public abstract class Report extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "report_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reporter_id")
  private Member reporter;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "reported_id")
  private Member reported;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private ReportCategory category;

  protected void setReporter(Member reporter) {
    this.reporter = reporter;
  }

  protected void setReported(Member reported) {
    this.reported = reported;
  }

  protected void setCategory(ReportCategory category) {
    this.category = category;
  }

}
