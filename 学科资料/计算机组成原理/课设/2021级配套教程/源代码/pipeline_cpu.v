`timescale 1ns / 1ns
`include "define.vh"
module pipeline_cpu (
    input wire clk,
    input wire rst,
    input wire [`RegBus] rom_data_i,
    output wire [`RegBus] rom_addr_o,
    output wire rom_ce_o,

    input wire [`RegBus] ram_data_i,
    output wire [`RegBus] ram_addr_o,
    output wire [`RegBus] ram_data_o,
    output wire ram_we_o,
    output wire [3:0] ram_sel_o,
    output wire [3:0] ram_ce_o
);

    // 连接if/id到ID
    wire [`InstAddrBus] pc;
    wire [`InstAddrBus] id_pc_i;
    wire [`InstBus] id_inst_i;

    // 连接id到id_ex
    wire [`AluOpBus] id_aluop_o;
    wire [`RegBus] id_reg1_o;
    wire [`RegBus] id_reg2_o;
    wire [`RegAddrBus] id_wd_o;
    wire id_wreg_o;
    wire [`RegBus] id_inst_o;

    wire id_is_in_delayslot_o;
    wire [`RegBus] id_link_address_o;

    // 连接id_ex到ex
    wire [`AluOpBus] ex_aluop_i;
    wire [`RegBus] ex_reg1_i;
    wire [`RegBus] ex_reg2_i;
    wire [`RegAddrBus] ex_wd_i;
    wire ex_wreg_i;
    wire [`RegBus] ex_inst_i;
    wire ex_is_in_delayslot_i;
    wire [`RegBus] ex_link_address_i;

    // 连接ex到ex_mem
    wire [`RegBus] ex_wdata_o;
    wire [`RegAddrBus] ex_wd_o;
    wire ex_wreg_o;
    wire [`AluOpBus] ex_aluop_o;
    wire [`RegBus] ex_mem_addr_o;
    wire [`RegBus] ex_reg2_o;
    // wire [`RegBus] ex_hi_o;
    // wire [`RegBus] ex_lo_o;
    // wire ex_whilo_o;

    // 连接ex_mem到mem
    wire [`RegBus] mem_wdata_i;
    wire [`RegAddrBus] mem_wd_i;
    wire mem_wreg_i;
    wire [`AluOpBus] mem_aluop_i;
    wire [`RegBus] mem_mem_addr_i;
    wire [`RegBus] mem_reg2_i;
    // wire [`RegBus] mem_hi_i;
    // wire [`RegBus] mem_lo_i;
    // wire mem_whilo_i;

    // 连接mem到mem_wb
    wire [`RegBus] mem_wdata_o;
    wire [`RegAddrBus] mem_wd_o;
    wire mem_wreg_o;
    // wire [`RegBus] mem_hi_o;
    // wire [`RegBus] mem_lo_o;
    // wire mem_whilo_o;

    // 连接mem_wb到regfile
    wire [`RegBus] wb_wdata_i;
    wire [`RegAddrBus] wb_wd_i;
    wire wb_wreg_i;
    // wire [`RegBus] wb_hi_i;
    // wire [`RegBus] wb_lo_i;
    // wire wb_whilo_i;

    // 连接id到regfile
    wire reg1_read;
    wire reg2_read;
    wire [`RegAddrBus] reg1_addr;
    wire [`RegAddrBus] reg2_addr;
    wire [`RegBus] reg1_data;
    wire [`RegBus] reg2_data;

    // 分支跳转相关
    wire is_in_delayslot_i;
    wire is_in_delayslot_o;
    wire next_inst_in_delayslot_o;
    wire id_branch_flag_o;
    wire [`RegBus] branch_target_address;

    // 流水暂停
    wire [5:0] stall;
    wire stallreq_from_id;
    wire stallreq_from_ex;

    // // div
    // wire[`DoubleRegBus] div_result;
    // wire div_ready;
    // wire[`RegBus] div_opdata1;
    // wire[`RegBus] div_opdata2;
    // wire div_start;
    // wire div_annul;
    // wire signed_div;

    // // mul
    // wire[`DoubleRegBus] mul_result;
    // wire mul_ready;
    // wire[`RegBus] mul_opdata1;
    // wire[`RegBus] mul_opdata2;
    // wire mul_start;
    // wire signed_mul;

    // HI LO reg
    // wire [`RegBus] hi;
    // wire [`RegBus] lo;



    assign rom_addr_o = pc;

    pc_reg pc_reg0 (
        .clk(clk),
        .rst(rst),
        .pc(pc),
        .ce(rom_ce_o),
        .branch_flag_i(id_branch_flag_o),
        .branch_target_address_i(branch_target_address),
        .stall(stall)
    );

    if_id if_id0 (
        .clk(clk),
        .rst(rst),
        .if_pc(pc),
        .if_inst(rom_data_i),
        .id_pc(id_pc_i),
        .id_inst(id_inst_i),
        .stall(stall)
    );

    id id0 (
        .rst(rst),
        .inst_i(id_inst_i),
        .aluop_o(id_aluop_o),
        .reg1_o(id_reg1_o),
        .reg2_o(id_reg2_o),
        .wd_o(id_wd_o),
        .wreg_o(id_wreg_o),
        .reg1_read_o(reg1_read),
        .reg1_addr_o(reg1_addr),
        .reg2_read_o(reg2_read),
        .reg2_addr_o(reg2_addr),
        .reg1_data_i(reg1_data),
        .reg2_data_i(reg2_data),
        .inst_o(id_inst_o),

        // 分支跳转
        .pc_i(id_pc_i),
        .is_in_delayslot_i(is_in_delayslot_i),
        .next_inst_in_delayslot_o(next_inst_in_delayslot_o),
        .branch_flag_o(id_branch_flag_o),
        .branch_target_address_o(branch_target_address),
        .link_addr_o(id_link_address_o),
        .is_in_delayslot_o(id_is_in_delayslot_o),

        // //执行阶段要写入寄存器数据
        // .ex_wreg_i (ex_wreg_o),
        // .ex_wdata_i(ex_wdata_o),
        // .ex_waddr_i(ex_wd_o),

        // //访存阶段要写入寄存器数据
        // .mem_wreg_i (mem_wreg_o),
        // .mem_wdata_i(mem_wdata_o),
        // .mem_waddr_i(mem_wd_o),

        .stallreq(stallreq_from_id)
    );

    id_ex id_ex0 (
        .rst(rst),
        .clk(clk),
        .id_aluop(id_aluop_o),
        .id_reg1(id_reg1_o),
        .id_reg2(id_reg2_o),
        .id_wd(id_wd_o),
        .id_wreg(id_wreg_o),
        .ex_aluop(ex_aluop_i),
        .ex_reg1(ex_reg1_i),
        .ex_reg2(ex_reg2_i),
        .ex_wd(ex_wd_i),
        .ex_wreg(ex_wreg_i),
        .id_inst(id_inst_o),
        .ex_inst(ex_inst_i),
        // 分支跳转相关
        .id_link_address(id_link_address_o),
        .id_is_in_delayslot(id_is_in_delayslot_o),
        .next_inst_in_delayslot_i(next_inst_in_delayslot_o),
        .ex_link_address(ex_link_address_i),
        .ex_is_in_delayslot(ex_is_in_delayslot_i),
        .is_in_delayslot_o(is_in_delayslot_i),
        .stall(stall)
    );

    alu alu0 (
        .rst(rst),
        .alu_control(ex_aluop_i),
        .alu_src1(ex_reg1_i),
        .alu_src2(ex_reg2_i),
        .wd_i(ex_wd_i),
        .wreg_i(ex_wreg_i),
        .alu_result(ex_wdata_o),
        .wd_o(ex_wd_o),
        .wreg_o(ex_wreg_o),
        .aluop_o(ex_aluop_o),
        .mem_addr_o(ex_mem_addr_o),
        .reg2_o(ex_reg2_o),
        .inst_i(ex_inst_i),
        // 分支跳转相关
        .link_address_i(ex_link_address_i),
        .is_in_delayslot_i(ex_is_in_delayslot_i),
        .stallreq(stallreq_from_ex)
        // div相关
        //   .div_result_i(div_result),
        // .div_ready_i(div_ready),
        // .div_opdata1_o(div_opdata1),
        // .div_opdata2_o(div_opdata2),
        // .div_start_o(div_start),
        // .signed_div_o(signed_div),
        // mul 相关
        // .mul_result_i(mul_result),
        // .mul_ready_i(mul_ready),
        // .mul_opdata1_o(mul_opdata1),
        // .mul_opdata2_o(mul_opdata2),
        // .mul_start_o(mul_start),
        // .signed_mul_o(signed_mul),
        // 移动指令 数据相关
        // .hi_i(hi),
        // .lo_i(lo),
        // .wb_hi_i(wb_hi_i),
        // .wb_lo_i(wb_lo_i),
        // .wb_whilo_i(wb_whilo_i),
        // .mem_hi_i(mem_hi_o),
        // .mem_lo_i(mem_lo_o),
        // .mem_whilo_i(mem_whilo_o),
        // .hi_o(ex_hi_o),
        // .lo_o(ex_lo_o),
        // .whilo_o(ex_whilo_o)
    );

    ex_mem ex_mem0 (
        .rst(rst),
        .clk(clk),
        .ex_wdata(ex_wdata_o),
        .ex_wd(ex_wd_o),
        .ex_wreg(ex_wreg_o),
        .mem_wdata(mem_wdata_i),
        .mem_wd(mem_wd_i),
        .mem_wreg(mem_wreg_i),
        .ex_aluop(ex_aluop_o),
        .ex_mem_addr(ex_mem_addr_o),
        .ex_reg2(ex_reg2_o),
        .mem_aluop(mem_aluop_i),
        .mem_mem_addr(mem_mem_addr_i),
        .mem_reg2(mem_reg2_i),
        .stall(stall)

        // .ex_hi(ex_hi_o),
        // .ex_lo(ex_lo_o),
        // .ex_whilo(ex_whilo_o),
        // .mem_hi(mem_hi_i),
        // .mem_lo(mem_lo_i),
        // .mem_whilo(mem_whilo_i)
    );

    mem mem0 (
        .rst(rst),
        .wdata_i(mem_wdata_i),
        .wd_i(mem_wd_i),
        .wreg_i(mem_wreg_i),
        .wdata_o(mem_wdata_o),
        .wd_o(mem_wd_o),
        .wreg_o(mem_wreg_o),
        .aluop_i(mem_aluop_i),
        .mem_addr_i(mem_mem_addr_i),
        .reg2_i(mem_reg2_i),
        .mem_data_i(ram_data_i),
        .mem_addr_o(ram_addr_o),
        .mem_we_o(ram_we_o),
        .mem_sel_o(ram_sel_o),
        .mem_data_o(ram_data_o),
        .mem_ce_o(ram_ce_o)
        // .hi_i(mem_hi_i),
        // .lo_i(mem_lo_i),
        // .whilo_i(mem_whilo_i),
        // .hi_o(mem_hi_o),
        // .lo_o(mem_lo_o),
        // .whilo_o(mem_whilo_o)
    );

    mem_wb mem_wb0 (
        .rst(rst),
        .clk(clk),
        .mem_wdata(mem_wdata_o),
        .mem_wd(mem_wd_o),
        .mem_wreg(mem_wreg_o),
        .wb_wdata(wb_wdata_i),
        .wb_wd(wb_wd_i),
        .wb_wreg(wb_wreg_i),
        .stall(stall)
        // .mem_hi(mem_hi_o),
        // .mem_lo(mem_lo_o),
        // .mem_whilo(mem_whilo_o),
        // .wb_hi(wb_hi_i),
        // .wb_lo(wb_lo_i),
        // .wb_whilo(wb_whilo_i)
    );

    // 暂停请求收集器
    ctrl ctrl0 (
        .rst(rst),
        .stallreq_from_id(stallreq_from_id),
        .stallreq_from_ex(stallreq_from_ex),
        .stall(stall)
    );

    // div div0 (
    //     .clk(clk),
    //     .rst(rst),
    //     .signed_div_i(signed_div),
    //     .opdata1_i(div_opdata1),
    //     .opdata2_i(div_opdata2),
    //     .start_i(div_start),
    //     .annul_i(1'b0),
    //     .result_o(div_result),
    //     .ready_o(div_ready)
    // );

    // mult mult0 (
    //     .clk(clk),
    //     .rst(rst),
    //     .start(mul_start),
    //     .mul_opdata1(mul_opdata1),
    //     .mul_opdata2(mul_opdata2),
    //     .mul_result(mul_result),
    //     .mul_ready_o(mul_ready),
    //     .signed_mul_i(signed_mul)
    // );

    // 这段已经注释掉了
    //multt multt0(.CLK(clk),.RSTn(rst),.START(mul_start),.A(mul_opdata1),.B(mul_opdata2),
    //    .RESULT(mul_result),.Done(mul_ready));

    // hilo_reg hilo_reg0 (
    //     .clk (clk),
    //     .rst (rst),
    //     .we  (wb_whilo_i),
    //     .hi_i(wb_hi_i),
    //     .lo_i(wb_lo_i),
    //     .hi_o(hi),
    //     .lo_o(lo)
    // );

    regfile regfile0 (
        .clk(clk),
        .rst(rst),
        .re1(reg1_read),
        .raddr1(reg1_addr),
        .re2(reg2_read),
        .raddr2(reg2_addr),
        .we(wb_wreg_i),
        .waddr(wb_wd_i),
        .wdata(wb_wdata_i),
        .rdata1(reg1_data),
        .rdata2(reg2_data)
    );


endmodule
