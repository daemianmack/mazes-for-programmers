module Seed
  def self.set(seed)
    seed = if seed
             seed.to_i
           else
             Random.new_seed
           end
    srand(seed)
    seed
  end
end