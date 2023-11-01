# 程序计数器PC
## 警告！警告！本仿真代码非课上代码，仅供与第三次实验完成模板对照使用！自行编写实验报告时务必修改！

## 设计

```verilog
`timescale 1ns / 1ps

module pc (
    input wire rst,
    input wire clk,
    output reg [4:0]pc=0,
    output reg ce=0
    );
    
    always@(posedge clk)begin
        if(rst== 1)begin
            ce<= 0; // 注意非阻塞赋值
        end else begin
            ce<= 1;
        end
    end
    
    always@(posedge clk)begin
        if(ce== 0)begin
            pc<= 0;
        end else begin
            pc<=pc+4'd4;
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
    wire [4:0] pc;
    wire ce;
    pc pc1(rst, clk, pc, ce);
    initial begin
        clk = 0;
        forever begin
        #5 clk = ~clk;
        end
    end
    
    initial begin
        rst = 1;
        #20 rst = 0;
        #40 $finish;
    end
endmodule
```


# MIPS寄存器堆

## 设计

```verilog
`timescale 1ns / 1ps
module regfile(

    input wire re1,
    input wire[4:0] raddr1,

    input wire re2,
    input wire[4:0] raddr2,
    input wire we,
    input wire[4:0] waddr,
    input wire[31:0] wdata,
    input wire rst,
    input wire clk,
    output reg[31:0] rdata1,
    output reg[31:0] rdata2
);
    reg[31:0] regs[31:0];
    
    always@(posedge clk)begin
        if(rst==0)begin
            if((we==1) && waddr!=0)begin
                regs[waddr]=wdata;
            end
        end
    end
    
    always@(*)begin
        if(rst== 1)begin
            rdata1 = 32'b0;
        end else if((re1== 1)&&(raddr1==0))begin
            rdata1 = 32'b0;
        end else if(re1== 1)begin
            rdata1 = regs[raddr1];
        end else begin
            rdata1=32'b0;
        end
    end
    
    always@(*)begin
        if(rst== 1)begin
            rdata2 = 32'b0;
        end else if((re2== 1)&&(raddr2==0))begin
            rdata2 = 32'b0;
        end else if(re2== 1)begin
            rdata2 = regs[raddr2];
        end else begin
            rdata2=32'b0;
        end
    end
endmodule

```

## 仿真

```verilog
`timescale 1ns / 1ps

module regfile_tb();
    reg re1;
    reg[4:0] raddr1;
    reg re2;
    reg[4:0] raddr2;
    reg we;
    reg[4:0] waddr;
    reg[31:0] wdata;
    reg rst;
    reg clk;
    
    wire[31:0] rdata1;
    wire[31:0] rdata2;
    
    regfile reg1(re1,raddr1,re2,raddr2,we,waddr,wdata,rst,clk,rdata1,rdata2);
    
    integer i=0;
    initial begin
        wdata=32'habcd5678;
        we=1;
        for(i=0;i<=31;i=i+1)begin
            waddr=i;
            wdata=wdata+32'h01010101;
            #15;
        end
        
        re1=1;
        re2=1;
        for(i=0;i<=31;i=i+1)begin
            raddr1=i;
            raddr2=31-i;
            #10;
        end
    end
    
    initial begin
        clk = 1;
        forever begin
            #5 clk = ~clk;
        end
    end
    
    initial begin
        rst=0;
        #100 rst=1;
        #50 rst=0;
        #600 rst=1;
        #100 rst=0;
        #100 $finish;
    end
   

endmodule

```
