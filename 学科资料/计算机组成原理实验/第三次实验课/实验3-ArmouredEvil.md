# 程序计数器PC

## 设计

```verilog
`timescale 1ns / 1ps

module pc(
    input wire rst,
    input wire clk,
    output reg[3:0] pc = 0,
    output reg ce
    );
    always@(posedge clk)begin
        if(rst == 0)begin
            ce = 1;
        end else begin
            ce = 0;
        end
    end
    always@(posedge clk)begin
        if(ce == 1)begin
            pc = pc + 4'd4;
        end else begin
            pc = 0;
        end
    end
endmodule
```

## 仿真

```verilog
`timescale 1ns / 1ps

module pc_tb();
    reg rst;
    reg clk;
    wire [3:0] pc;
    wire ce;
    pc pc_tb(rst, clk, pc, ce);
    
    initial begin
        clk = 0;
        forever begin
            clk = ~clk;
            #5;
        end
    end
    initial begin
        rst = 0;
        #20 
        rst = 1;
        #10 
        $finish;
    end
endmodule

```

# MIPS寄存器堆

见老师课上代码，或Niefire的代码