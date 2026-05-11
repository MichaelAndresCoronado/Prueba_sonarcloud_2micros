package ec.edu.espe.logiflow.taller.restdos.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ordenes_mantenimiento")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrdenMantenimiento {

    @Id
    private UUID id;

    @Column(nullable = false, length = 20)
    private String matricula;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String estado = "REGISTRADA";

    @PrePersist
    protected void onCreate() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        this.fechaRegistro = LocalDateTime.now();
        this.matricula = this.matricula.toUpperCase();
    }
}
