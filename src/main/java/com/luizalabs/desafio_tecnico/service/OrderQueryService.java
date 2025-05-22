package com.luizalabs.desafio_tecnico.service;

import com.luizalabs.desafio_tecnico.dto.OrderDTO;
import com.luizalabs.desafio_tecnico.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderQueryService {
    private final List<UserDTO> pedidosProcessados;

    public OrderQueryService() {

        this.pedidosProcessados = new ArrayList<>();
    }

    public List<UserDTO> consultarPedidos(Optional<Long> orderId, Optional<LocalDate> dataInicio, Optional<LocalDate> dataFim) {
        return pedidosProcessados.stream()
                .map(user -> {
                    List<OrderDTO> pedidosFiltrados = user.getOrders().stream()
                            .filter(order -> orderId.map(id -> id.equals(order.getId())).orElse(true)) // CORRIGIDO: usar equals()
                            .filter(order -> {
                                LocalDate dataPedido = LocalDate.parse(order.getDate());
                                boolean depoisDataInicio = dataInicio.map(d -> !dataPedido.isBefore(d)).orElse(true);
                                boolean antesDataFim = dataFim.map(d -> !dataPedido.isAfter(d)).orElse(true);
                                return depoisDataInicio && antesDataFim;
                            })
                            .collect(Collectors.toList());

                    if (pedidosFiltrados.isEmpty()) return null;

                    UserDTO u = new UserDTO();
                    u.setId(user.getId());
                    u.setName(user.getName());
                    u.setOrders(pedidosFiltrados);
                    return u;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void salvarPedidos(List<UserDTO> novosPedidos) {
        pedidosProcessados.clear();
        pedidosProcessados.addAll(novosPedidos);
    }

    public List<UserDTO> buscarTodosUsuarios() {
        return new ArrayList<>(pedidosProcessados);
    }

    public Optional<UserDTO> buscarUsuarioPorId(Long userId) {
        return pedidosProcessados.stream()
                .filter(user -> user.getId().equals(userId))
                .findFirst();
    }

    public void limparDados() {
        pedidosProcessados.clear();
    }

    public List<UserDTO> buscarUsuariosOrdenados(String orderBy, String direction) {
        Comparator<UserDTO> comparator;

        switch (orderBy.toLowerCase()) {
            case "name":
                comparator = Comparator.comparing(UserDTO::getName);
                break;
            case "id":
            default:
                comparator = Comparator.comparing(UserDTO::getId);
                break;
        }

        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }

        return pedidosProcessados.stream()
                .sorted(comparator)
                .toList();
    }
}