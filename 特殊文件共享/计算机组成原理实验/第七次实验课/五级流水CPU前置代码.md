# 五级流水CPU前置代码

PS：PPT上if_id的箭头指向有问题，但老师写的代码（应该）没问题；mem（存储器）未作为实验课任务，此md中的mem代码系个人所写，未必正确，谨慎参考。

## if_id

```verilog
`timescale 1ns / 1ps

module if_id(
    input wire clk,
    input wire[31:0] if_inst,
    output reg[31:0] id_inst
    );
    always@(posedge clk)begin
        id_inst <= if_inst;
    end
endmodule
```

## id_ex

```verilog
`timescale 1ns / 1ps

module id_ex(
    input wire clk,
    input wire[3:0] id_aluop,
    input wire[31:0] id_reg1,
    input wire[31:0] id_reg2,
    input wire[4:0] id_wd,
    input wire id_wreg,
    output reg[3:0] ex_aluop,
    output reg[31:0] ex_reg1,
    output reg[31:0] ex_reg2,
    output reg[4:0] ex_wd,
    output reg ex_wreg
    );
    always@(posedge clk)begin
        ex_aluop <= id_aluop;
        ex_reg1 <= id_reg1;
        ex_reg2 <= id_reg2;
        ex_wd <= id_wd;
        ex_wreg <= id_wreg;
    end
endmodule

```

## ex_mem

```verilog
`timescale 1ns / 1ps

module ex_mem(
    input wire clk,
    input wire[31:0] ex_wdata,
    input wire[4:0] ex_wd,
    input wire ex_wreg,
    output reg[31:0] mem_data,
    output reg[4:0] mem_wd,
    output reg mem_wreg
    );
    always@(posedge clk)begin
        mem_data <= ex_wdata;
        mem_wd <= ex_wd;
        mem_wreg <= ex_wreg;
    end
endmodule
```

## mem 注意不要借鉴这个，根据图示，这里应该要添加一个（模拟）外存

```verilog
`timescale 1ns / 1ps

module mem(
    input wire[31:0] wdata_i,
    input wire[4:0] wd_i,
    input wire wreg_i,
    output reg[31:0] wdata_o,
    output reg[4:0] wd_o,
    output reg wreg_o
    );
    always@(*)begin
        wdata_o = wdata_i;
        wd_o = wd_i;
        wreg_o = wreg_i;
    end
endmodule

```

## mem_wb

```verilog
`timescale 1ns / 1ps

module mem_wb(
    input wire clk,
    input wire[31:0] mem_wdata,
    input wire[4:0] mem_wd,
    input wire mem_wreg,
    output reg[31:0] wb_wdata,
    output reg[4:0] wb_wd,
    output reg wb_wreg
    );
    always@(posedge clk)begin
        wb_wdata <= mem_wdata;
        wb_wd <= mem_wd;
        wb_wreg <= mem_wreg;
    end
endmodule
```

