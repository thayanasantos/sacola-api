package me.dio.sacola.service.impl;

import lombok.RequiredArgsConstructor;
import me.dio.sacola.enumeration.FormaPagamento;
import me.dio.sacola.model.Item;
import me.dio.sacola.model.Restaurante;
import me.dio.sacola.model.Sacola;
import me.dio.sacola.repository.ItemRepository;
import me.dio.sacola.repository.ProdutoRepository;
import me.dio.sacola.repository.SacolaRepository;
import me.dio.sacola.resource.dto.ItemDto;
import me.dio.sacola.service.SacolaService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class SacolaServiceImpl implements SacolaService {


    private final SacolaRepository sacolaRepository;
    private final ProdutoRepository produtoRepository;
private final ItemRepository itemRepository;
    @Override
    public Item incluirItemNaSacola(ItemDto itemDto) {
        Sacola sacola = verSacola(itemDto.getSacolaId());
        if (sacola.isFechada()) {
            throw new RuntimeException("Essa sacola está fechada");

        }
        Item itemParaSerInserido = Item.builder()
                .quantidade(itemDto.getQuantidade())
                .sacola(sacola)
                .produto(produtoRepository.findById(itemDto.getProdutoId()).orElseThrow(
                        () -> {
                            throw new RuntimeException("Essa Sacola não existe!");
                        }
                ))
                .build();
        List<Item> itensDaSacola = sacola.getItens();
        if(itensDaSacola.isEmpty()){
            itensDaSacola.add(itemParaSerInserido);
        }else {
            Restaurante restauranteatual = itensDaSacola.get(0).getProduto().getRestaurante();
            Restaurante restauranteDoItensParaAdicionar = itemParaSerInserido.getProduto().getRestaurante();
       if(restauranteatual.equals(restauranteDoItensParaAdicionar)) {
           itensDaSacola.add(itemParaSerInserido);
       }else {
           throw new RuntimeException("nao é possivel adicionar produyos de restaurantes dierente, feche a sacola ou esvazie");
       }
               }

        List<Double> valorDosItens = new ArrayList<>();
        for (Item itemDaSacola: itensDaSacola) {
            double valorTotalItem = itemDaSacola.getProduto().getValorUnitario() * itemDaSacola.getQuantidade();
            valorDosItens.add(valorTotalItem);
        }
        double valorTotalSacola = valorDosItens.stream()
                .mapToDouble(valorTotalDeCadaItem -> valorTotalDeCadaItem)
                .sum();

        sacola.setValorTotal(valorTotalSacola);
        sacolaRepository.save(sacola) ;

        return itemParaSerInserido;
    }

        @Override
        public Sacola verSacola (Long id){
            return sacolaRepository.findById(id).orElseThrow(
                    () -> {
                        throw new RuntimeException("Essa Sacola não existe!");

                    }
            );
        }

        @Override
        public Sacola fecharSacola (Long id,int numeroFormaPagamento){
            Sacola sacola = verSacola(id);
            if (sacola.getItens().isEmpty()) {
                throw new RuntimeException("Inclua itens na sacola!");
            }

            FormaPagamento formaPagamento =
                    numeroFormaPagamento == 0 ? FormaPagamento.DINHEIRO : FormaPagamento.MAQUINETA;

            sacola.setFormaPagamento(formaPagamento);
            sacola.setFechada(true);
            return sacolaRepository.save(sacola);


        }
    }
