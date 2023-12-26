`timescale 1ns / 1ns
`include "define.vh"
module id(
    input wire rst,
    // 当前指令地址
    input wire[`InstAddrBus]	pc_i,
    // 指令 32位
    input wire[`InstBus] inst_i,
    // 读 寄存器 的值 32位
    input wire[`RegBus] reg1_data_i,
    input wire[`RegBus] reg2_data_i,
    // 两个读寄存器的读使能信号
    output reg reg1_read_o,
    output reg reg2_read_o,
    // 两个读寄存器的地址 
    output reg[`RegAddrBus] reg1_addr_o,
    output reg[`RegAddrBus] reg2_addr_o,
    // 运算类型 4位 -> 扩展至6位
    output reg[`AluOpBus] aluop_o,
    // 运算源操作数 1（在寄存器）32位
    output reg[`RegBus] reg1_o,
    // 运算源操作数 2
    output reg[`RegBus] reg2_o,
    // 译码阶段指令要写入的目的寄存器地址 5位
    output reg[`RegAddrBus] wd_o,
    // 是否输出至寄存器
    output reg wreg_o,
    
    output wire[`RegBus] inst_o, // 指令输出口
    
    
    // 分支跳转相关
    input wire is_in_delayslot_i, // 上一条转移指令，下一条指令进入译码时，若是延迟槽则true
    output reg next_inst_in_delayslot_o,  
    output reg branch_flag_o,
    output reg[`RegBus] branch_target_address_o,
    output reg[`RegBus] link_addr_o,
    output reg is_in_delayslot_o,
    
    //数据相关解决方案
    // 执行阶段的输入
    // input wire[`RegAddrBus] ex_waddr_i,
    // input wire ex_wreg_i,
    // input wire[`RegBus] ex_wdata_i,

    // // 访存阶段的输入
    // input wire mem_wreg_i,
    // input wire[`RegAddrBus] mem_waddr_i,
    // input wire[`RegBus]  mem_wdata_i,
    
    // 暂时不用
    output wire stallreq	
    
    );
    
    assign stallreq = `NoStop;
    
    // 传递指令
    assign inst_o = inst_i;
    
    wire[`RegBus] pc_plus_8;
    wire[`RegBus] pc_plus_4;
    wire[`RegBus] imm_sll2_signedext;
    assign pc_plus_8 = pc_i + 8;
    assign pc_plus_4 = pc_i + 4;
    // 转移目标由立即数 offset 左移 2 位,并进行有符号扩展
    assign imm_sll2_signedext = {{14{inst_i[15]}}, inst_i[15:0], 2'b00};
    
    // OP
    wire[5:0] op = inst_i[31:26];
    wire[4:0] op2 = inst_i[10:6];
    wire[5:0] op3 = inst_i[5:0];
    wire[4:0] op4 = inst_i[20:16];
    
    // 保存立即数
    reg[`RegBus] imm;
    // 指令合法？
    reg instvalid;
    
        always @(*) begin
            // 复位出现，全部恢复初态
            if(rst == `RstEnable) begin
                aluop_o <= `NOP_OP;
                wd_o <= `NOPRegAddr;
                wreg_o <=`WriteDisable;
                instvalid <= `InstValid;
                reg1_read_o <=1'b0;
                reg2_read_o <= 1'b0;
                // NOPRegAddr b'00000
                reg1_addr_o <= `NOPRegAddr;
                reg2_addr_o <= `NOPRegAddr;
                imm <= 32'h0;
                
                link_addr_o <= `ZeroWord;
                branch_target_address_o <= `ZeroWord;
                branch_flag_o <= `NotBranch;
                next_inst_in_delayslot_o <= `NotInDelaySlot;
            end else begin
                aluop_o <= `NOP_OP;
                // 写入的寄存器地址 去看指令格式
                wd_o <= inst_i[15:11];
                // 初始化 禁写
                wreg_o <= `WriteDisable;
                instvalid <= `InstInvalid;
                // 初始化 禁读
                reg1_read_o <= 1'b0;
                reg2_read_o <= 1'b0;
                // 读出的寄存器地址 去看指令格式
                reg1_addr_o <= inst_i[25:21];
                reg2_addr_o <= inst_i[20:16];
                // 立即数初始化为 0 
                imm <= `ZeroWord;
                
                aluop_o <= `NOP_OP;
                link_addr_o <= `ZeroWord;
                branch_target_address_o <= `ZeroWord;
                branch_flag_o <= `NotBranch;
                next_inst_in_delayslot_o <= `NotInDelaySlot;
                // ----------- 开始译码 ------------
                // 判断操作数 31：26
                case (op)
                    // b'000000
                    `EXE_SPECIAL_INST: begin
                        // 判断操作数 10:6
                        case (op2)
                            5'b00000: begin
                                // 判断操作数 5:0
                                case(op3)
                                    `EXE_OR: begin
                                        wreg_o <=`WriteEnable;
                                        aluop_o <= `OR_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                    end
                                   `EXE_AND: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `AND_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_XOR: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `XOR_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_NOR: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `NOR_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_SLT: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `SLT_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_SLTU: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `SLTU_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_ADD: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `ADD_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_ADDU:begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `ADDU_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_SUB: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `SUB_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_SUBU:begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `SUBU_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                    end
                                    `EXE_JR: begin
                                        wreg_o <= `WriteDisable;
                                        aluop_o <= `JR_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b0;
                                        instvalid <= `InstValid;
                                        // 不需要返回地址
                                        link_addr_o <= `ZeroWord;
                                        branch_target_address_o <= reg1_o;
                                        branch_flag_o <= `Branch; 
                                        next_inst_in_delayslot_o <= `InDelaySlot;
                                    end
                                    `EXE_SLLV: begin
                                         wreg_o <= `WriteEnable;
                                         aluop_o <= `SLLV_OP;
                                         reg1_read_o <=1'b1;
                                         reg2_read_o <= 1'b1;
                                         instvalid <= `InstValid;
                                    end
                                    `EXE_SRAV: begin
                                         wreg_o <= `WriteEnable;
                                         aluop_o <= `SRAV_OP;
                                         reg1_read_o <=1'b1;
                                         reg2_read_o <= 1'b1;
                                         instvalid <= `InstValid;
                                    end
                                     `EXE_SRLV: begin
                                        wreg_o <= `WriteEnable;
                                        aluop_o <= `SRLV_OP;
                                        reg1_read_o <=1'b1;
                                        reg2_read_o <= 1'b1;
                                        instvalid <= `InstValid;
                                   end
                                   `EXE_MULT: begin
                                       wreg_o <= `WriteDisable;
                                       aluop_o <= `MULT_OP;
                                       reg1_read_o <=1'b1;
                                       reg2_read_o <= 1'b1;
                                       instvalid <= `InstValid;
                                  end
                                  `EXE_MULTU: begin
                                      wreg_o <= `WriteDisable;
                                      aluop_o <= `MULTU_OP;
                                      reg1_read_o <=1'b1;
                                      reg2_read_o <= 1'b1;
                                      instvalid <= `InstValid;
                                 end
                                  `EXE_DIV: begin
                                      wreg_o <= `WriteDisable;
                                      aluop_o <= `DIV_OP;
                                      reg1_read_o <=1'b1;
                                      reg2_read_o <= 1'b1;
                                      instvalid <= `InstValid;
                                 end
                                 `EXE_DIVU: begin
                                     wreg_o <= `WriteDisable;
                                     aluop_o <= `DIVU_OP;
                                     reg1_read_o <=1'b1;
                                     reg2_read_o <= 1'b1;
                                     instvalid <= `InstValid;
                                end
                                `EXE_MFHI: begin
                                     wreg_o <= `WriteEnable;
                                     aluop_o <= `MFHI_OP;
                                     reg1_read_o <=1'b0;
                                     reg2_read_o <= 1'b0;
                                     instvalid <= `InstValid;
                                end
                                `EXE_MFLO: begin
                                     wreg_o <= `WriteEnable;
                                     aluop_o <= `MFLO_OP;
                                     reg1_read_o <=1'b0;
                                     reg2_read_o <= 1'b0;
                                     instvalid <= `InstValid;
                                end
                                `EXE_MTHI: begin
                                     wreg_o <= `WriteDisable;
                                     aluop_o <= `MTHI_OP;
                                     reg1_read_o <=1'b1;
                                     reg2_read_o <= 1'b0;
                                     instvalid <= `InstValid;
                                end
                                `EXE_MTLO: begin
                                     wreg_o <= `WriteDisable;
                                     aluop_o <= `MTLO_OP;
                                     reg1_read_o <=1'b1;
                                     reg2_read_o <= 1'b0;
                                     instvalid <= `InstValid;
                                end
                                   default: begin end
                               endcase // op3
                           end
                           default: begin end
                       endcase // op2
                   end
                   // b'001111
                   `EXE_LUI: begin
                       wreg_o <= `WriteEnable;
                       aluop_o <= `OR_OP;
                       reg1_read_o <=1'b1;
                       reg2_read_o <= 1'b0;
                       imm <= {inst_i[15:0],16'h0};
                       wd_o <= inst_i[20:16];
                       instvalid <= `InstValid;
                  end
                  `EXE_ADDIU:begin
                        wreg_o <= `WriteEnable;
                        aluop_o <= `ADDIU_OP;
                        reg1_read_o <= 1'b1;
                        reg2_read_o <= 1'b0;
                        imm <= {{16{inst_i[15]}},inst_i[15:0]};
                        wd_o <= inst_i[20:16];
                        instvalid <= `InstValid;
                    end
                    `EXE_LW:begin
                        wreg_o <= `WriteEnable;
                        aluop_o <= `LW_OP;
                        reg1_read_o <= 1'b1;
                        reg2_read_o <= 1'b0;
                        wd_o <= inst_i[20:16];
                        instvalid <= `InstValid;
                    end
                    `EXE_SW:begin
                        wreg_o <= `WriteDisable;
                        aluop_o <= `SW_OP;
                        reg1_read_o <= 1'b1;
                        reg2_read_o <= 1'b1;
                        instvalid <= `InstValid;
                    end
                    `EXE_JAL: begin
                        aluop_o <= `JAL_OP;
                        wreg_o <= `WriteEnable;
                        // 延迟槽指令之后的指令的 PC 值写入31号寄存器中
                        wd_o <= 5'b11111;
                        reg1_read_o <= 1'b0;
                        reg2_read_o <= 1'b0;
                        instvalid <= `InstValid;
                        // 返回地址
                        link_addr_o <= pc_plus_8;
                        // 跳转目标由该分支指令对应的延迟槽指令的 PC 的最高 4 位与立即数 instr_index 左移
                        // 2 位后的值拼接得到
                        branch_target_address_o <= {pc_plus_4[31:28], inst_i[25:0], 2'b00};
                        branch_flag_o <= `Branch;
                        next_inst_in_delayslot_o <= `InDelaySlot;
                    end
                    `EXE_BEQ: begin
                        aluop_o <= `BEQ_OP;
                        wreg_o <= `WriteDisable;
                        reg1_read_o <= 1'b1;
                        reg2_read_o <= 1'b1;
                        instvalid <= `InstValid;
                        if (reg1_o == reg2_o) begin
                            // 转移目的地址
                            branch_target_address_o <= pc_plus_4 + imm_sll2_signedext;
                            branch_flag_o <= `Branch;
                            // 下一条指令是延迟槽指令
                            next_inst_in_delayslot_o <= `InDelaySlot;
                        end
                    end
                    `EXE_BNE: begin
                        aluop_o <= `BNE_OP;
                        instvalid <= `InstValid;
                        wreg_o <= `WriteDisable;
                        reg1_read_o <= 1'b1;
                        reg2_read_o <= 1'b1;
                        if (reg1_o != reg2_o) begin
                            branch_target_address_o <= pc_plus_4 + imm_sll2_signedext;
                            branch_flag_o <= `Branch;
                            next_inst_in_delayslot_o <= `InDelaySlot;
                        end
                    end
                    `EXE_ADDI: begin
                        aluop_o <= `ADDI_OP;
                        wreg_o <= `WriteEnable;
                        wd_o <= inst_i[20:16];
                        reg1_read_o <= `ReadEnable;
                        reg2_read_o <= `ReadDisable;
                        imm <= {{16{inst_i[15]}},inst_i[15:0]};
                        instvalid <= `InstValid;
                     end
                       `EXE_SLTI: begin
                        wreg_o <= `WriteEnable;
                        aluop_o <= `SLTI_OP;
                        reg1_read_o <=1'b1;
                        reg2_read_o <= 1'b0;
                        imm <= {{16{inst_i[15]}},inst_i[15:0]};
                        wd_o <= inst_i[20:16];
                        instvalid <= `InstValid;
                     end
                      `EXE_SLTIU: begin
                         wreg_o <= `WriteEnable;
                         aluop_o <= `SLTIU_OP;
                         reg1_read_o <=1'b1;
                         reg2_read_o <= 1'b0;
                         imm <= {{16{inst_i[15]}},inst_i[15:0]};
                         wd_o <= inst_i[20:16];
                         instvalid <= `InstValid;
                    end
                     `EXE_ANDI: begin
                         wreg_o <= `WriteEnable;
                         aluop_o <= `ANDI_OP;
                         reg1_read_o <=1'b1;
                         reg2_read_o <= 1'b0;
                         imm <= {16'h0,inst_i[15:0]};
                         wd_o <= inst_i[20:16];
                         instvalid <= `InstValid;
                    end
                     `EXE_ORI: begin
                         wreg_o <= `WriteEnable;
                         aluop_o <= `ORI_OP;
                         reg1_read_o <=1'b1;
                         reg2_read_o <= 1'b0;
                         imm <= {16'h0,inst_i[15:0]};
                         wd_o <= inst_i[20:16];
                         instvalid <= `InstValid;
                    end
                    `EXE_XORI: begin
                         wreg_o <= `WriteEnable;
                         aluop_o <= `XORI_OP;
                         reg1_read_o <=1'b1;
                         reg2_read_o <= 1'b0;
                         imm <= {16'h0,inst_i[15:0]};
                         wd_o <= inst_i[20:16];
                         instvalid <= `InstValid;
                    end
                  default: begin end
              endcase // OP1
              
              if (inst_i[31:21] == 11'b00000000000) begin
                if(op3 == `EXE_SLL) begin
                    wreg_o <= `WriteEnable;
                    aluop_o <= `SLL_OP;
                    reg1_read_o <= 1'b0;
                    reg2_read_o <= 1'b1;
                    imm[4:0] <= inst_i[10:6];
                    wd_o <= inst_i[15:11];
                    instvalid <= `InstValid;
                end else if (op3 == `EXE_SRL ) begin
                    wreg_o <= `WriteEnable;
                    aluop_o <= `SRL_OP;
                    reg1_read_o <= 1'b0;
                    reg2_read_o <= 1'b1;
                    imm[4:0] <= inst_i[10:6];
                    wd_o <= inst_i[15:11];
                    instvalid <= `InstValid;
                end else if ( op3 == `EXE_SRA ) begin
                    wreg_o <= `WriteEnable;
                    aluop_o <= `SRA_OP;
                    reg1_read_o <= 1'b0;
                    reg2_read_o <= 1'b1;
                    imm[4:0] <= inst_i[10:6];
                    wd_o <= inst_i[15:11];
                    instvalid <= `InstValid;
                end
            end
        end
    end      
    
    always @(*) begin
        if (rst == `RstEnable) begin
            is_in_delayslot_o <= `NotInDelaySlot;
        end
        else begin
        // 当前译码指令是否是延迟槽指令
            is_in_delayslot_o <= is_in_delayslot_i;
        end
   end
    
    always @(*) begin
        if ( rst == `RstEnable) begin
            reg1_o <= `ZeroWord;
            // 如果读取的寄存器是执行阶段要写的寄存器，那么读取的寄存器直接是
            // 写入的值
        // end else if ((reg1_read_o == 1'b1)&&(ex_wreg_i==1'b1)&&(ex_waddr_i==reg1_addr_o)) begin
        //     reg1_o <= ex_wdata_i;
        //     // 如果读取的寄存器就是访存阶段要写的寄存器，
        //     // 那么直接把访存的结果作为寄存器的值
        // end else if((reg1_read_o==1'b1)&&(mem_wreg_i==1'b1)&&(mem_waddr_i==reg1_addr_o)) begin
        //     reg1_o <= mem_wdata_i;
        end else if((reg1_read_o==1'b1)) begin
            reg1_o <= reg1_data_i;
        end else if((reg1_read_o == 1'b0)) begin
            // reg1 充当立即数
            reg1_o <= imm;
        end else begin
            reg1_o <= `ZeroWord;
        end
    end
    
    // ------解决了数据相关---------
    always @(*) begin
            if ( rst == `RstEnable) begin
                reg2_o <= `ZeroWord;
                // 如果读取的寄存器是执行阶段要写的寄存器，那么读取的寄存器直接是
                // 写入的值
            // end else if ((reg2_read_o == 1'b1)&&(ex_wreg_i==1'b1)&&(ex_waddr_i==reg2_addr_o)) begin
            //     reg2_o <= ex_wdata_i;
            //     // 如果读取的寄存器就是访存阶段要写的寄存器，
            //     // 那么直接把访存的结果作为寄存器的值
            // end else if((reg2_read_o==1'b1)&&(mem_wreg_i==1'b1)&&(mem_waddr_i==reg2_addr_o)) begin
            //     reg2_o <= mem_wdata_i;
            end else if(reg2_read_o==1'b1) begin
                reg2_o <= reg2_data_i;
            end else if(reg2_read_o == 1'b0) begin
                // reg1 充当立即数
                reg2_o <= imm;
            end else begin
                reg2_o <= `ZeroWord;
            end
        end
    
    

              
              
                   
endmodule
